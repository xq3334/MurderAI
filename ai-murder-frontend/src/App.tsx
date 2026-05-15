import { useEffect, useMemo, useRef, useState, type MouseEvent, type ReactNode } from 'react'
import type { KeyboardEvent } from 'react'
import {
  fetchChatHints,
  fetchSessionDetail,
  initializeRandomScriptSession,
  listScripts,
  streamChat,
  submitFinalAccusation,
  type ChatHintResponse,
  type ChatStreamProgressResponse,
  type ChatStreamStructuredMessage,
  type EndingRevealResponse,
  type ScriptSummaryResponse,
  type SessionCharacterSeatResponse,
} from './api'
import { GameExperience } from './GameExperience'
import './App.css'

type AppView = 'landing' | 'setup' | 'game'

type CharacterSeat = {
  name: string
  role: string
  mood: string
  status: string
}

type ChatMessage = {
  id: string
  speaker: string
  speakerKey: string
  role: 'system' | 'player' | 'character' | 'narrator' | 'pending'
  kind: 'opening' | 'dialogue' | 'clue' | 'scene'
  tone?: string
  content: string
}

type RevealDirection = 'up' | 'left' | 'right'

type RevealSectionProps = {
  children: ReactNode
  className?: string
  delay?: number
  direction?: RevealDirection
}

type Hotspot = {
  id: string
  x: string
  y: string
  title: string
  description: string
}

type ScriptDisplayMeta = {
  label: string
  value: string
}

type ScriptFeature = {
  title: string
  description: string
}

type GameThemeKey = 'default' | 'manor' | 'campus' | 'harbor'

const characterSeats: CharacterSeat[] = [
  { name: '林乔', role: '财务顾问', mood: '冷静得近乎反常', status: '越追问账目细节，越可能露出破绽' },
  { name: '顾深', role: '律师', mood: '克制而防备', status: '对晚宴前的争执始终轻描淡写' },
  { name: '周衍', role: '线路承包代表', mood: '心虚多于愤怒', status: '一提停电时间点就会明显闪躲' },
  { name: '陆沉', role: '死者侄子', mood: '压抑冷硬', status: '像在等别人先暴露真正意图' },
]

const openingMessages: ChatMessage[] = [
  {
    id: 'local-system-1',
    speaker: '系统',
    speakerKey: 'system',
    role: 'system',
    kind: 'opening',
    tone: '引导',
    content: '你已进入《雨夜断灯》体验场。这里会按角色、旁白和线索三种层级实时落消息，不再把整段信息挤进一个气泡里。',
  },
]

const featureCards = [
  {
    eyebrow: 'Immersive Cast',
    title: '角色真实在场',
    description: '每个角色都有自己的立场、秘密与语气，发言像真正围坐在你面前，而不是统一口吻的回复。',
  },
  {
    eyebrow: 'Dynamic Story',
    title: '剧情随问而动',
    description: '你的每一次追问都会改变现场气氛，让剧情沿着不同方向展开，而不是照着单一路线走完。',
  },
  {
    eyebrow: 'Clue Space',
    title: '线索逐步浮现',
    description: '重要信息不会一股脑塞给你，而是在合适的时机被揭开，让推理过程更像真实破局。',
  },
  {
    eyebrow: 'Live Tension',
    title: '多人同场博弈',
    description: '不是一个人在讲故事，而是所有人都在场、都在掩饰、也都可能在你一句话后露出破绽。',
  },
]

const flowSteps = [
  '进入副本，迅速知道自己是谁、身处什么局中',
  '点名发问，让不同角色依次开口、互相牵制',
  '随着对话深入，隐藏线索与矛盾逐渐浮出水面',
  '在不断变化的现场里，拼出属于你的真相路径',
]

const progressHighlights = [
  { label: '体验定位', value: 'AI 沉浸式剧本杀' },
  { label: '核心感受', value: '像真的在盘一场局' },
  { label: '产品方向', value: '多剧本持续扩展' },
]

const heroMetrics = [
  { value: 'Multi', label: '多角色同场演绎' },
  { value: '3', label: '三层消息结构' },
  { value: 'Live', label: '进度与线索联动' },
]

const previewHotspots: Hotspot[] = [
  {
    id: 'timeline',
    x: '23%',
    y: '27%',
    title: '剧情状态节点',
    description: '每个副本都可以配置自己的阶段、触发条件和推进节奏，由后端状态控制统一驱动。',
  },
  {
    id: 'ledger',
    x: '52%',
    y: '38%',
    title: '线索对象化',
    description: '线索不应只是聊天文字，而应该沉淀成独立对象，方便展示、归档和后续组合推理。',
  },
  {
    id: 'motion',
    x: '76%',
    y: '61%',
    title: '角色行为反馈',
    description: '玩家发问不仅触发回复，还能影响角色压力、怀疑程度与后续可公开的信息。',
  },
]

const previewAnalysis = [
  {
    eyebrow: 'Scene Feeling',
    title: '让玩家先进入局，再进入操作',
    description: '好的剧本杀体验不是先看到功能，而是先被气氛裹进去，再自然开始发问、怀疑和推进。',
  },
  {
    eyebrow: 'Story Rhythm',
    title: '让信息出现得刚刚好',
    description: '不是一次性把答案摊开，而是让玩家在角色发言、细节反应与线索浮现之间，慢慢逼近真相。',
  },
]

const previewTranscript = [
  '【旁白】灯影晃动，空气里像压着一句谁也不愿先说出口的话。',
  '【玩家】先别急着看别人，告诉我，你刚才到底在躲什么？',
  '【角色】我没有躲，只是有些事情，不该由我第一个说出来。',
]

function getInitialView(): AppView {
  if (typeof window === 'undefined') {
    return 'landing'
  }
  if (window.location.hash === '#play') {
    return 'game'
  }
  if (window.location.hash === '#setup') {
    return 'setup'
  }
  return 'landing'
}

function mapStructuredRole(role: ChatStreamStructuredMessage['role']): ChatMessage['role'] {
  if (role === 'SYSTEM') return 'system'
  if (role === 'NARRATOR') return 'narrator'
  if (role === 'PLAYER') return 'player'
  return 'character'
}

function mapStructuredKind(kind: ChatStreamStructuredMessage['kind']): ChatMessage['kind'] {
  if (kind === 'OPENING') return 'opening'
  if (kind === 'CLUE') return 'clue'
  if (kind === 'SCENE') return 'scene'
  return 'dialogue'
}

function getScriptDisplayMeta(script: ScriptSummaryResponse | null): ScriptDisplayMeta[] {
  if (!script) {
    return []
  }

  return [
    { label: '玩法', value: script.playerModeName },
    { label: '视角', value: script.randomRoleOnStart ? '随机身份' : '固定主视角' },
    { label: '人数', value: `${script.selectableRoleCount}` },
    { label: '顺序', value: `${script.unlockOrder}` },
  ]
}

function getScriptFeatures(script: ScriptSummaryResponse | null): ScriptFeature[] {
  if (!script) {
    return []
  }

  if (script.scriptId === 'summer-evening-cicadas') {
    return [
      { title: '校园关系场', description: '围绕关系、试探和逐场推进的调查节奏展开。' },
      { title: '固定主视角', description: '从稳定的玩家视角进入，更适合复现问题和做流程调试。' },
      { title: '行动触发', description: '这个副本已经接好了查看地点、跟进线索之类的场景动作。' },
    ]
  }

  if (script.scriptId === 'fog-harbor-letter') {
    return [
      { title: '封港旧案', description: '更强调身份误导和缓慢施压的悬疑感。' },
      { title: '随机开局', description: '适合从不同角色视角重复测试同一个副本。' },
      { title: '氛围优先', description: '适合验证旁白语气和线索逐步揭露的效果。' },
    ]
  }

  return [
    { title: '封闭山庄案', description: '结构直接，适合测试阶段推进和最终指认。' },
    { title: '稳定基线', description: '想快速验证主流程时，这个本最适合作为默认基准。' },
    { title: '位置压力', description: '更强调时间线、不在场证明和多人对质。' },
  ]
}

function toUserFacingErrorMessage(message: string) {
  const normalized = message.toLowerCase()
  if (normalized.includes('connection reset')) {
    return '连接中断了，请稍后再试。'
  }
  if (normalized.includes('timeout')) {
    return '请求超时了，请稍后再试。'
  }
  return message
}

function resolveGameTheme(scriptId: string | null | undefined): GameThemeKey {
  if (scriptId === 'summer-evening-cicadas') {
    return 'campus'
  }

  if (scriptId === 'fog-harbor-letter') {
    return 'harbor'
  }

  if (scriptId === 'rainy-night-blackout') {
    return 'manor'
  }

  return 'default'
}

function resolveSetupCardTheme(scriptId: string): GameThemeKey {
  return resolveGameTheme(scriptId)
}

function RevealSection({ children, className, delay = 0, direction = 'up' }: RevealSectionProps) {
  const ref = useRef<HTMLDivElement | null>(null)
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    const element = ref.current
    if (!element) {
      return
    }

    const observer = new IntersectionObserver(
      ([entry]) => {
        setVisible(entry.isIntersecting)
      },
      { threshold: 0.26 },
    )

    observer.observe(element)
    return () => observer.disconnect()
  }, [])

  const revealClass = direction === 'left' ? 'reveal-left' : direction === 'right' ? 'reveal-right' : 'reveal-up'

  return (
    <div ref={ref} className={`reveal-section ${visible ? 'is-visible' : ''} ${className ?? ''}`}>
      <div className={`reveal ${revealClass}`} style={{ ['--delay' as string]: `${delay}ms` }}>
        {children}
      </div>
    </div>
  )
}

function App() {
  const [view, setView] = useState<AppView>(() => getInitialView())
  const [draft, setDraft] = useState('')
  const [messages, setMessages] = useState<ChatMessage[]>(openingMessages)
  const [progress, setProgress] = useState<ChatStreamProgressResponse | null>(null)
  const [sessionId, setSessionId] = useState('')
  const [currentScriptId, setCurrentScriptId] = useState('')
  const [isStreaming, setIsStreaming] = useState(false)
  const [sessionSeats, setSessionSeats] = useState<SessionCharacterSeatResponse[]>([])
  const [scripts, setScripts] = useState<ScriptSummaryResponse[]>([])
  const [selectedScriptId, setSelectedScriptId] = useState('')
  const [scriptsLoading, setScriptsLoading] = useState(false)
  const [scriptsError, setScriptsError] = useState('')
  const [isStartingScript, setIsStartingScript] = useState(false)
  const [accusedCharacterId, setAccusedCharacterId] = useState('')
  const [reasoning, setReasoning] = useState('')
  const [isAccusing, setIsAccusing] = useState(false)
  const [ending, setEnding] = useState<EndingRevealResponse | null>(null)
  const [hints, setHints] = useState<ChatHintResponse['hints']>([])
  const [quickActions, setQuickActions] = useState<string[]>([])
  const [isHintsOpen, setIsHintsOpen] = useState(false)
  const [isHintsLoading, setIsHintsLoading] = useState(false)
  const [hintError, setHintError] = useState('')
  const [activeHotspotId, setActiveHotspotId] = useState(previewHotspots[0].id)
  const [previewPointer, setPreviewPointer] = useState({
    tiltX: '-3deg',
    tiltY: '5deg',
    x: '58%',
    y: '42%',
  })

  const previewMessage = useMemo(() => {
    const activeHotspot = previewHotspots.find((item) => item.id === activeHotspotId) ?? previewHotspots[0]
    return activeHotspot
  }, [activeHotspotId])

  const selectedScript = useMemo(
    () => scripts.find((item) => item.scriptId === selectedScriptId) ?? scripts[0] ?? null,
    [scripts, selectedScriptId],
  )

  const selectedScriptMeta = useMemo(() => getScriptDisplayMeta(selectedScript), [selectedScript])
  const selectedScriptFeatures = useMemo(() => getScriptFeatures(selectedScript), [selectedScript])
  const currentThemeKey = useMemo(() => resolveGameTheme(currentScriptId || selectedScript?.scriptId), [currentScriptId, selectedScript])

  useEffect(() => {
    const handleHashChange = () => {
      setView(getInitialView())
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    window.addEventListener('hashchange', handleHashChange)
    return () => window.removeEventListener('hashchange', handleHashChange)
  }, [])

  useEffect(() => {
    let cancelled = false

    async function loadScripts() {
      setScriptsLoading(true)
      setScriptsError('')
      try {
        const items = await listScripts()
        if (cancelled) {
          return
        }
        setScripts(items)
        setSelectedScriptId((current) => {
          if (current && items.some((item) => item.scriptId === current)) {
            return current
          }
          return items[0]?.scriptId ?? ''
        })
      } catch (error) {
        if (!cancelled) {
          setScriptsError(error instanceof Error ? error.message : '副本加载失败。')
        }
      } finally {
        if (!cancelled) {
          setScriptsLoading(false)
        }
      }
    }

    void loadScripts()

    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    if (!sessionId) {
      return
    }

    let cancelled = false

    async function loadSessionDetail() {
      try {
        const detail = await fetchSessionDetail(sessionId)
        if (!cancelled) {
          setSessionSeats(detail.characterSeats)
          setProgress(detail.progress)
        }
      } catch {
        if (!cancelled) {
          setSessionSeats([])
        }
      }
    }

    void loadSessionDetail()

    return () => {
      cancelled = true
    }
  }, [sessionId])

  useEffect(() => {
    if (view === 'game' && !sessionId && window.location.hash !== '#setup') {
      window.location.hash = 'setup'
    }
  }, [sessionId, view])

  const openSetup = (event?: MouseEvent<HTMLAnchorElement | HTMLButtonElement>) => {
    event?.preventDefault()
    window.location.hash = 'setup'
  }

  const openGame = (event?: MouseEvent<HTMLAnchorElement | HTMLButtonElement>) => {
    openSetup(event)
  }

  const backToLanding = () => {
    window.location.hash = ''
  }

  const backToScriptLibrary = () => {
    window.location.hash = 'setup'
  }

  const displaySeats = useMemo(() => {
    if (sessionSeats.length) {
      return sessionSeats.map((seat) => ({
        name: seat.characterName,
        role: seat.identity,
        mood: seat.mood,
        status: seat.status,
      }))
    }
    return characterSeats
  }, [sessionSeats])

  const canAccuse = Boolean(progress && progress.currentStageOrder >= progress.totalStages)

  const accusationOptions = useMemo(() => {
    return sessionSeats.map((seat) => ({
      value: seat.characterId,
      label: seat.characterName,
      description: seat.identity,
    }))
  }, [sessionSeats])

  const upsertStructuredMessage = (incoming: ChatStreamStructuredMessage) => {
    setMessages((current) => {
      const index = current.findIndex((message) => message.id === incoming.messageId)
      if (index >= 0) {
        const next = [...current]
        next[index] = {
          ...next[index],
          content: next[index].content + incoming.delta,
          tone: incoming.tone,
          kind: mapStructuredKind(incoming.kind),
          role: mapStructuredRole(incoming.role),
        }
        return next
      }

      return [
        ...current,
        {
          id: incoming.messageId,
          speaker: incoming.speaker,
          speakerKey: incoming.speakerKey,
          role: mapStructuredRole(incoming.role),
          kind: mapStructuredKind(incoming.kind),
          tone: incoming.tone,
          content: incoming.delta,
        },
      ]
    })
  }

  const sendMessage = async (message: string) => {
    const trimmed = message.trim()
    if (!trimmed || isStreaming) {
      return
    }

    setHints([])
    setHintError('')
    setIsHintsOpen(false)
    setQuickActions([])

    setMessages((current) => [
      ...current,
      {
        id: `player-${Date.now()}`,
        speaker: '你',
        speakerKey: 'player',
        role: 'player',
        kind: 'dialogue',
        content: trimmed,
      },
    ])

    setDraft('')
    setIsStreaming(true)

    try {
      setEnding(null)
      await streamChat(
        {
          sessionId: sessionId || undefined,
          message: trimmed,
        },
        {
          onEvent: (eventName, payload) => {
            if (!payload.success || !payload.data) {
              if (eventName === 'error') {
                setMessages((current) => [
                  ...current,
                  {
                    id: `error-${Date.now()}`,
                    speaker: '系统',
                    speakerKey: 'system',
                    role: 'system',
                    kind: 'dialogue',
                    tone: '错误',
                    content: toUserFacingErrorMessage(payload.message || '请求失败'),
                  },
                ])
              }
              return
            }

            const data = payload.data
            setSessionId(data.sessionId)
            if (data.progress) {
              setProgress(data.progress)
            }

            if (eventName === 'message' && data.message) {
              upsertStructuredMessage(data.message)
              if (data.message.kind === 'SCENE') {
                setQuickActions(data.message.quickActions ?? [])
              }
            }
          },
        },
      )
    } catch (error) {
      const failureMessage = toUserFacingErrorMessage(error instanceof Error ? error.message : '连接现场失败')
      setMessages((current) => [
        ...current,
        {
          id: `error-${Date.now()}`,
          speaker: '系统',
          speakerKey: 'system',
          role: 'system',
          kind: 'dialogue',
          tone: '错误',
          content: failureMessage,
        },
      ])
    } finally {
      setIsStreaming(false)
    }
  }

  const handleSend = async () => {
    await sendMessage(draft)
  }

  const handleUseQuickAction = async (action: string) => {
    await sendMessage(action)
  }

  const handleAccuse = async () => {
    if (!sessionId || !accusedCharacterId || isAccusing) {
      return
    }

    setIsAccusing(true)
    try {
      const reveal = await submitFinalAccusation({
        sessionId,
        accusedCharacterId,
        reasoning: reasoning.trim() || undefined,
      })
      setEnding(reveal)
    } catch (error) {
      const failureMessage = toUserFacingErrorMessage(error instanceof Error ? error.message : '最终指认提交失败')
      setMessages((current) => [
        ...current,
        {
          id: `accuse-error-${Date.now()}`,
          speaker: '系统',
          speakerKey: 'system',
          role: 'system',
          kind: 'dialogue',
          tone: '错误',
          content: failureMessage,
        },
      ])
    } finally {
      setIsAccusing(false)
    }
  }

  const handleToggleHints = async () => {
    if (isHintsOpen) {
      setIsHintsOpen(false)
      return
    }

    setIsHintsOpen(true)

    if (!sessionId) {
      setHints([])
      setHintError('请先进入一个副本，再请求提示。')
      return
    }

    if (hints.length || isHintsLoading) {
      return
    }

    setIsHintsLoading(true)
    setHintError('')
    try {
      const response = await fetchChatHints({ sessionId })
      setHints(response.hints)
    } catch (error) {
      setHints([])
      setHintError(error instanceof Error ? error.message : '提示加载失败。')
    } finally {
      setIsHintsLoading(false)
    }
  }

  const handleUseHint = (hint: string) => {
    setDraft(hint)
    setIsHintsOpen(false)
  }

  const handleStartScript = async () => {
    if (!selectedScript || scriptsLoading || isStartingScript) {
      return
    }

    setIsStartingScript(true)
    setScriptsError('')

    try {
      const bootstrap = await initializeRandomScriptSession({ scriptId: selectedScript.scriptId })
      const detail = await fetchSessionDetail(bootstrap.sessionId)

      setSessionId(bootstrap.sessionId)
      setCurrentScriptId(bootstrap.scriptId)
      setSessionSeats(detail.characterSeats)
      setProgress(detail.progress)
      setAccusedCharacterId('')
      setReasoning('')
      setEnding(null)
      setHints([])
      setQuickActions([])
      setHintError('')
      setIsHintsOpen(false)
      setMessages([
        {
          id: `opening-system-${bootstrap.sessionId}`,
          speaker: '系统',
          speakerKey: 'system',
          role: 'system',
          kind: 'opening',
          tone: '入局',
          content: `你已进入《${bootstrap.scriptName}》，当前身份为${bootstrap.playerCharacterName}。`,
        },
        {
          id: `opening-scene-${bootstrap.sessionId}`,
          speaker: '旁白',
          speakerKey: 'narrator',
          role: 'narrator',
          kind: 'scene',
          tone: '开场',
          content: bootstrap.openingNarration,
        },
        {
          id: `opening-role-${bootstrap.sessionId}`,
          speaker: bootstrap.playerCharacterName,
          speakerKey: 'player',
          role: 'player',
          kind: 'opening',
          tone: '身份',
          content: bootstrap.playerRoleDescription,
        },
      ])

      window.location.hash = 'play'
    } catch (error) {
      setScriptsError(error instanceof Error ? error.message : '进入副本失败。')
    } finally {
      setIsStartingScript(false)
    }
  }

  const handlePreviewEnter = (event: KeyboardEvent<HTMLElement>) => {
    if (event.key !== 'Enter' && event.key !== ' ') {
      return
    }

    event.preventDefault()
    void handleStartScript()
  }

  const handlePreviewMove = (event: MouseEvent<HTMLDivElement>) => {
    const rect = event.currentTarget.getBoundingClientRect()
    const x = (event.clientX - rect.left) / rect.width
    const y = (event.clientY - rect.top) / rect.height
    const tiltX = `${((0.5 - y) * 8).toFixed(2)}deg`
    const tiltY = `${((x - 0.5) * 10).toFixed(2)}deg`

    setPreviewPointer({
      tiltX,
      tiltY,
      x: `${(x * 100).toFixed(2)}%`,
      y: `${(y * 100).toFixed(2)}%`,
    })
  }

  const resetPreviewMove = () => {
    setPreviewPointer({
      tiltX: '-3deg',
      tiltY: '5deg',
      x: '58%',
      y: '42%',
    })
  }

  if (view === 'setup' || (view === 'game' && !sessionId)) {
    return (
      <div className="setup-shell">
        <div className="setup-shell__curtain setup-shell__curtain--left" />
        <div className="setup-shell__curtain setup-shell__curtain--right" />
        <div className="setup-shell__inner">
          <div className="setup-topbar">
            <button type="button" className="hero__button" onClick={backToLanding}>
              返回首页
            </button>
            <div className="setup-topbar__title">
              <span>副本选择</span>
              <strong>先从当前副本里选一个，再正式进入这一局。</strong>
            </div>
          </div>

          <div className="setup-layout">
            <section className="setup-panel setup-panel--catalog">
              <div className="setup-panel__header">
                <span>副本目录</span>
                <h2>可用副本</h2>
              </div>
              <div className="setup-script-list">
                {scriptsLoading ? <p>正在加载副本...</p> : null}
                {!scriptsLoading && !scripts.length ? <p>当前没有可用副本。</p> : null}
                {scripts.map((script) => (
                  <button
                    key={script.scriptId}
                    type="button"
                    className={`setup-script-card setup-script-card--${resolveSetupCardTheme(script.scriptId)} ${selectedScriptId === script.scriptId ? 'setup-script-card--active' : ''}`}
                    onClick={() => setSelectedScriptId(script.scriptId)}
                  >
                    <div className="setup-script-card__topline">
                      <span>{script.playerModeName}</span>
                      <em>{script.randomRoleOnStart ? '随机身份' : '固定身份'}</em>
                    </div>
                    <strong>{script.scriptName}</strong>
                    <p>{script.summary}</p>
                    <div className="setup-script-card__stats">
                      <span>{script.selectableRoleCount} 个席位</span>
                      <span>第 {script.unlockOrder} 部</span>
                    </div>
                  </button>
                ))}
              </div>
            </section>

            <section
              className={`setup-panel setup-panel--preview ${selectedScript ? 'setup-panel--clickable' : ''}`}
              role={selectedScript ? 'button' : undefined}
              tabIndex={selectedScript ? 0 : undefined}
              onClick={() => {
                void handleStartScript()
              }}
              onKeyDown={handlePreviewEnter}
              aria-disabled={!selectedScript || scriptsLoading || isStartingScript}
            >
              <div className="setup-panel__header">
                <span>副本预览</span>
                <h2>{selectedScript?.scriptName ?? '请选择一个副本'}</h2>
              </div>
              {selectedScript ? (
                <>
                  <div className="setup-preview-spotlight">
                    <p>{selectedScript.openingNarration}</p>
                  </div>
                  <div className="setup-preview-meta">
                    {selectedScriptMeta.map((item) => (
                      <div key={item.label} className="setup-preview-meta__item">
                        <span>{item.label}</span>
                        <strong>{item.value}</strong>
                      </div>
                    ))}
                  </div>
                  <div className="setup-summary__hint">
                    <strong>{selectedScript.playerModeName}</strong>
                    <p>{selectedScript.summary}</p>
                  </div>
                  <div className="setup-feature-list">
                    {selectedScriptFeatures.map((feature) => (
                      <article key={feature.title} className="setup-feature-card">
                        <strong>{feature.title}</strong>
                        <p>{feature.description}</p>
                      </article>
                    ))}
                  </div>
                  {scriptsError ? <div className="setup-summary__error">{scriptsError}</div> : null}
                  <div className="preview-transcript">
                    <div className="preview-transcript__system">这里会先预览入局初始化、开场旁白和身份分配方式。</div>
                    <div className="preview-transcript__player">调试时先选中你真正想进的那个副本，再从这里开局。</div>
                    <div className="preview-transcript__character">这样就不会一加载就默认掉进第一个本。</div>
                  </div>
                </>
              ) : (
                <p>先选择一个副本，再在这里查看它的预览信息。</p>
              )}
            </section>

          </div>
        </div>
      </div>
    )
  }

  if (view === 'game') {
    return (
      <GameExperience
        messages={messages}
        progress={progress}
        themeKey={currentThemeKey}
        draft={draft}
        isStreaming={isStreaming}
        onDraftChange={setDraft}
        onSend={handleSend}
        onBack={backToScriptLibrary}
        characterSeats={displaySeats}
        canAccuse={canAccuse}
        accusationOptions={accusationOptions}
        accusedCharacterId={accusedCharacterId}
        onAccusedCharacterChange={setAccusedCharacterId}
        reasoning={reasoning}
        onReasoningChange={setReasoning}
        onAccuse={handleAccuse}
        isAccusing={isAccusing}
        ending={ending}
        hints={hints}
        quickActions={quickActions}
        isHintsOpen={isHintsOpen}
        isHintsLoading={isHintsLoading}
        hintError={hintError}
        onToggleHints={handleToggleHints}
        onUseHint={handleUseHint}
        onUseQuickAction={handleUseQuickAction}
      />
    )
  }

  return (
    <div className="landing-shell">
      <section className="hero">
        <div className="hero__curtain hero__curtain--left" />
        <div className="hero__curtain hero__curtain--right" />
        <div className="hero__curtain-glow" />
        <div className="hero__image-wrap">
          <img src="/night-manor.png" alt="暴雨中的山庄夜景" className="hero__image" />
        </div>

        <header className="hero__topbar">
          <span className="hero__mark">AI 剧本杀体验原型</span>
          <nav className="hero__nav">
            <a href="#overview">概览</a>
            <a href="#mechanism">机制</a>
            <a href="#setup" onClick={openSetup}>
              选择副本
            </a>
            <a href="#play" onClick={openGame}>
              进入选本
            </a>
          </nav>
        </header>

        <div className="hero__content">
          <div className="hero__copy">
            <span className="hero__eyebrow">四字命名提案</span>
            <h1>夜幕疑局</h1>
            <p className="hero__lede">
              这是一款把多角色对话、悬疑氛围、线索推进和自由追问融合在一起的 AI 剧本杀体验，让你像真的坐在一场局中。
            </p>

            <div className="hero__actions">
              <button type="button" className="hero__button hero__button--primary" onClick={openGame}>
                选择副本
              </button>
              <button type="button" className="hero__button" onClick={openSetup}>
                副本库
              </button>
              <a href="#mechanism" className="hero__button">
                查看机制
              </a>
            </div>

            <div className="hero__ribbon">
              <span />
              <p>你看到的不是一个普通聊天页，而是一个能承载多个故事、多种人物关系和不同推理节奏的剧本杀产品入口。</p>
            </div>

            <dl className="hero__stats">
              {progressHighlights.map((item) => (
                <div key={item.label}>
                  <dt>{item.label}</dt>
                  <dd>{item.value}</dd>
                </div>
              ))}
            </dl>
          </div>

          <aside className="hero__sidebar">
            <div className="hero-note">
              <span>Current Experience</span>
              <strong>沉浸、试探、反转与推理并行发生</strong>
              <p>从开场氛围到角色对话，再到逐步浮现的线索与真相，这里强调的是一场局的完整体验，而不是单次问答。</p>
            </div>
            <div className="hero-pulse">
              {flowSteps.map((step, index) => (
                <div key={step} className="hero-pulse__row" style={{ animationDelay: `${index * 120}ms` }}>
                  <span>{`0${index + 1}`}</span>
                  <p>{step}</p>
                </div>
              ))}
            </div>
            <div className="hero-metrics">
              {heroMetrics.map((metric, index) => (
                <div key={metric.label} className="hero-metrics__item" style={{ animationDelay: `${1.62 + index * 0.12}s` }}>
                  <strong>{metric.value}</strong>
                  <span>{metric.label}</span>
                </div>
              ))}
            </div>
          </aside>
        </div>
      </section>

      <section id="overview" className="band band--mechanism">
        <div className="band__inner">
          <RevealSection className="section-copy" direction="up">
            <>
              <span className="section-copy__eyebrow">Overview</span>
              <h2>从“会聊天”往“会控场”推进，重点不是多说，而是让每种信息站回自己的舞台位置。</h2>
              <p>好的 AI 剧本杀，不只是有人回复你，而是要让角色、旁白、线索和情绪都各自成立，最后共同撑起一场完整的悬疑体验。</p>
            </>
          </RevealSection>

          <div className="dossier-grid">
            {featureCards.map((card, index) => (
              <RevealSection key={card.title} delay={index * 90} direction="up">
                <article className="dossier-card dossier-card--active">
                  <span>{card.eyebrow}</span>
                  <strong>{card.title}</strong>
                  <p>{card.description}</p>
                </article>
              </RevealSection>
            ))}
          </div>

          <RevealSection direction="up">
            <div className="quote-stage">
              <div className="quote-stage__frame">
                <span className="quote-stage__eyebrow">Direction</span>
                <h3>真正有记忆点的剧本杀体验，不在于台词多少，而在于你是否真的感到每个人都藏着话、每句话都带着重量。</h3>
                <blockquote>首页不剧透任何一个具体副本，它只负责告诉玩家：这里能装下很多故事，而每个故事都值得被认真盘一遍。</blockquote>
              </div>
            </div>
          </RevealSection>
        </div>
      </section>

      <section id="mechanism" className="band band--preview">
        <div className="band__inner preview-layout">
          <RevealSection className="preview-copy" direction="left">
            <>
              <span className="section-copy__eyebrow">Mechanism</span>
              <h2>无论是现代悬疑、古风迷局还是科幻封闭空间，这个产品想做的，都是让玩家和角色一起把现场盘活。</h2>
              <p>这一屏展示的是剧本杀该有的氛围、节奏和互动感，而不是某一个固定故事。后面你做多个副本，这里依旧成立。</p>

              <div className="preview-analysis">
                {previewAnalysis.map((item) => (
                  <article key={item.title}>
                    <span>{item.eyebrow}</span>
                    <strong>{item.title}</strong>
                    <p>{item.description}</p>
                  </article>
                ))}
              </div>

              <div className="preview-transcript">
                <div className="preview-transcript__system">{previewTranscript[0]}</div>
                <div className="preview-transcript__player">{previewTranscript[1]}</div>
                <div className="preview-transcript__character">{previewTranscript[2]}</div>
              </div>
            </>
          </RevealSection>

          <RevealSection direction="right">
            <div className="preview-visual-stack">
              <div
                className="preview-visual preview-visual--active"
                onMouseMove={handlePreviewMove}
                onMouseLeave={resetPreviewMove}
              >
                <div
                  className="preview-visual__frame"
                  style={{
                    ['--tilt-x' as string]: previewPointer.tiltX,
                    ['--tilt-y' as string]: previewPointer.tiltY,
                  }}
                >
                  <img src="/evidence-board.png" alt="证据板插画" className="preview-visual__image" />
                  <div
                    className="preview-visual__glow"
                    style={{
                      ['--preview-x' as string]: previewPointer.x,
                      ['--preview-y' as string]: previewPointer.y,
                    }}
                  />
                  <div className="preview-visual__scanline" />
                  <div className="preview-visual__paper-cluster">
                    <div className="paper-card paper-card--a" />
                    <div className="paper-card paper-card--b" />
                    <div className="paper-card paper-card--c" />
                    <div className="paper-card paper-card--d" />
                    <div className="paper-card paper-card--e" />
                    <div className="paper-card paper-card--f" />
                  </div>
                  <div className="preview-visual__thread thread--top-left" />
                  <div className="preview-visual__thread thread--top-right" />
                  <div className="preview-visual__thread thread--center-drop" />
                  <div className="preview-visual__thread thread--bottom-left" />
                  <div className="preview-visual__thread thread--bottom-right" />
                  <div
                    className="preview-visual__cursor-lens"
                    style={{
                      ['--preview-x' as string]: previewPointer.x,
                      ['--preview-y' as string]: previewPointer.y,
                    }}
                  />
                  {previewHotspots.map((item) => (
                    <button
                      key={item.id}
                      type="button"
                      className={`preview-hotspot ${activeHotspotId === item.id ? 'preview-hotspot--active' : ''}`}
                      style={{
                        ['--hotspot-x' as string]: item.x,
                        ['--hotspot-y' as string]: item.y,
                      }}
                      onMouseEnter={() => setActiveHotspotId(item.id)}
                      aria-label={item.title}
                    >
                      <span />
                    </button>
                  ))}
                  <div className="preview-visual__detail-tag">
                    <strong>{previewMessage.title}</strong>
                    <p>{previewMessage.description}</p>
                  </div>
                </div>
              </div>

              <div className="preview-caption">
                <span>Interactive Board</span>
                <p>鼠标移过这个区域时，视角、扫光和热点说明会一起变化。它呈现的是“推理现场”的感觉，而不是某一个具体案件的细节。</p>
              </div>
            </div>
          </RevealSection>
        </div>
      </section>

      <section className="band band--closing">
        <div className="band__inner closing-layout">
          <RevealSection className="closing-copy" direction="left">
            <>
              <span className="section-copy__eyebrow">Entry</span>
              <h2>进入游戏后，用户看到的不该只是一个能回复的对话框，而是一个会随着自己发问而不断变化的悬疑现场。</h2>
              <p>所以结尾这块保留成更有戏感的落章票面。它负责收束首页，也负责把“剧本杀”的仪式感和调性钉牢。</p>
            </>
          </RevealSection>

          <RevealSection direction="right">
            <div className="closing-visual">
              <div className="closing-visual__card">
                <div className="closing-visual__strip closing-visual__strip--left" />
                <div className="closing-visual__strip closing-visual__strip--right" />
                <div className="closing-visual__glint" />
                <div className="closing-visual__ticket">
                  <span>Case Admission</span>
                  <strong>今夜，真相不会自己浮出来。</strong>
                  <p>你会看到角色说话、线索落下、目标更新，以及每一次追问对局势施加的真实压力。</p>
                  <div className="closing-visual__imprint">疑</div>
                </div>
                <div className="closing-visual__seal" aria-hidden="true">
                  <div className="closing-visual__seal-handle" />
                  <div className="closing-visual__seal-neck" />
                  <div className="closing-visual__seal-base" />
                  <div className="closing-visual__seal-wave" />
                </div>
              </div>
              <div className="closing-visual__caption">
                <span>Final Cue</span>
                <p>把鼠标放上去，仍然会有印章下落、票面压痕和印记显现。这个动作更像“入局”前的一记落印。</p>
              </div>
              <div className="closing-panel">
                <div className="closing-panel__line" />
                <strong>直接进入当前版本</strong>
                <p>首页负责建立这款产品的气氛和想象，进入游戏后再去承载具体副本。这样后面扩展剧本库，整体调性也不会散。</p>
                <a href="#play" onClick={openGame}>
                  选择副本
                </a>
              </div>
            </div>
          </RevealSection>
        </div>
      </section>
    </div>
  )
}

export default App
