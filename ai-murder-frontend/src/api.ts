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
export type StructuredMessageKind = 'OPENING' | 'DIALOGUE' | 'CLUE'

export type ChatStreamStructuredMessage = {
  messageId: string
  speaker: string
  speakerKey: string
  role: StructuredMessageRole
  kind: StructuredMessageKind
  tone: string
  delta: string
  completed: boolean
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

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

type StreamChatHandlers = {
  onEvent: (eventName: string, payload: Result<ChatStreamEventResponse>) => void
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
      handlers.onEvent(eventName, parsed)
    }
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
