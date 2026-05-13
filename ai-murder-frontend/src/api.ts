export type Result<T> = {
  success: boolean
  code: string
  message: string
  data: T | null
}

export type ChatStreamRequest = {
  sessionId?: string
  message: string
}

export type StructuredMessageRole = 'SYSTEM' | 'PLAYER' | 'CHARACTER' | 'NARRATOR'
export type StructuredMessageKind = 'OPENING' | 'DIALOGUE' | 'CLUE' | 'SCENE'

export type ChatStreamStructuredMessage = {
  messageId: string
  speaker: string
  speakerKey: string
  role: StructuredMessageRole
  kind: StructuredMessageKind
  tone: string
  delta: string
  completed: boolean
  quickActions?: string[]
}

export type ClueProgressItem = {
  clueId: string
  clueName: string
  content: string
  keyClue: boolean
}

export type ChatStreamProgressResponse = {
  scriptName: string
  playerRoleName: string
  currentStageName: string
  currentStageOrder: number
  totalStages: number
  objective: string
  atmosphere?: string
  storyBeat?: string
  playerTurnCount?: number
  revealedClues: ClueProgressItem[]
}

export type ChatStreamEventResponse = {
  event: 'start' | 'chunk' | 'message' | 'complete' | 'error'
  sessionId: string
  content: string
  completed: boolean
  message: ChatStreamStructuredMessage | null
  progress: ChatStreamProgressResponse | null
}

export type ScriptSummaryResponse = {
  scriptId: string
  scriptName: string
  summary: string
  openingNarration: string
  playerModeName: string
  selectableRoleCount: number
  unlockOrder: number
  randomRoleOnStart: boolean
}

export type SessionBootstrapResponse = {
  sessionId: string
  scriptId: string
  scriptName: string
  playerCharacterId: string
  playerCharacterName: string
  playerIdentity: string
  playerRoleDescription: string
  playerObjective: string
  openingNarration: string
}

export type SessionCharacterSeatResponse = {
  characterId: string
  characterName: string
  identity: string
  mood: string
  status: string
}

export type SessionDetailResponse = {
  sessionId: string
  scriptId: string
  scriptName: string
  playerCharacterId: string
  playerCharacterName: string
  playerIdentity: string
  playerRoleDescription: string
  playerObjective: string
  openingDelivered: boolean
  characterSeats: SessionCharacterSeatResponse[]
  progress: ChatStreamProgressResponse
}

export type FinalAccusationRequest = {
  sessionId: string
  accusedCharacterId: string
  reasoning?: string
}

export type EndingRevealResponse = {
  sessionId: string
  scriptId: string
  scriptName: string
  endingTitle: string
  success: boolean
  accusationAllowed: boolean
  verdict: string
  playerOutcome: string
  accusedCharacterName: string
  killerCharacterName: string
  reasoningSummary: string
  truthStory: string
  keyEvidence: string[]
}

export type ChatHintResponse = {
  sessionId: string
  hints: string[]
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

type StreamChatHandlers = {
  onEvent: (eventName: string, payload: Result<ChatStreamEventResponse>) => void
}

async function requestJson<T>(path: string, init?: RequestInit) {
  const response = await fetch(`${API_BASE_URL}${path}`, init)
  const payload = (await response.json().catch(() => null)) as Result<T> | null

  if (!response.ok || !payload?.success || !payload.data) {
    throw new Error(payload?.message ?? '请求失败')
  }

  return payload.data
}

export async function listScripts() {
  return requestJson<ScriptSummaryResponse[]>('/api/scripts')
}

export async function initializeRandomScriptSession(payload: { sessionId?: string; scriptId: string }) {
  return requestJson<SessionBootstrapResponse>('/api/scripts/random-select', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export async function fetchSessionDetail(sessionId: string) {
  return requestJson<SessionDetailResponse>(`/api/scripts/sessions/${sessionId}`)
}

export async function submitFinalAccusation(payload: FinalAccusationRequest) {
  return requestJson<EndingRevealResponse>('/api/ending/accuse', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export async function fetchChatHints(payload: { sessionId: string }) {
  return requestJson<ChatHintResponse>('/api/chat/hints', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export async function streamChat(payload: ChatStreamRequest, handlers: StreamChatHandlers) {
  const response = await fetch(`${API_BASE_URL}/api/chat/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'text/event-stream',
    },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    const failure = (await response.json().catch(() => ({ message: '请求失败' }))) as { message?: string }
    throw new Error(failure.message ?? '请求失败')
  }

  if (!response.body) {
    throw new Error('未获取到流式响应')
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''
  let reading = true
  let receivedServerError = false

  try {
    while (reading) {
      const { value, done } = await reader.read()
      if (done) {
        reading = false
        continue
      }

      buffer += decoder.decode(value, { stream: true })
      const blocks = buffer.split('\n\n')
      buffer = blocks.pop() ?? ''

      for (const block of blocks) {
        const eventName = extractEventName(block)
        const dataText = extractEventData(block)
        if (!dataText) {
          continue
        }

        const parsed = JSON.parse(dataText) as Result<ChatStreamEventResponse>
        if (eventName === 'error' || parsed.code === 'STREAM_ERROR') {
          receivedServerError = true
        }
        handlers.onEvent(eventName, parsed)
      }
    }
  } catch (error) {
    if (receivedServerError) {
      return
    }
    throw error instanceof Error ? error : new Error('连接现场失败')
  } finally {
    reader.releaseLock()
  }
}

function extractEventName(block: string) {
  const line = block
    .split('\n')
    .find((item) => item.startsWith('event:'))

  return line ? line.replace('event:', '').trim() : 'message'
}

function extractEventData(block: string) {
  return block
    .split('\n')
    .filter((item) => item.startsWith('data:'))
    .map((item) => item.replace('data:', '').trim())
    .join('\n')
}
