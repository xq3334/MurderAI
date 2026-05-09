import { useEffect, useMemo, useRef, useState, type MouseEvent, type ReactNode } from 'react'
import { type ChatStreamProgressResponse, type ChatStreamStructuredMessage, streamChat } from './api'
import { GameExperience } from './GameExperience'
import './App.css'

type AppView = 'landing' | 'game'

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

const characterSeats: CharacterSeat[] = [
  { name: '林乔', role: '财务顾问', mood: '冷静得近乎反常', status: '越追问账目细节，越可能露出破绽' },
  { name: '顾深', role: '律师', mood: '克制而防备', status: '对晚宴前的争执始终轻描淡写' },
  { name: '周衍', role: '线路承包代表', mood: '心虚多于愤怒', status: '一提停电时间点就会明显闪躲' },
  { name: '陆沉', role: '死者侄子', mood: '压抑冷硬', status: '像在等别人先暴露真正意图' },
]

const quickPrompts = [
  '先完整介绍这个副本、我的身份和第一轮目标。',
  '让在场四个人分别说一句，他们现在最想隐瞒什么。',
  '先告诉我停电前后最关键的时间线。',
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
  return window.location.hash === '#play' ? 'game' : 'landing'
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
  const [isStreaming, setIsStreaming] = useState(false)
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

  useEffect(() => {
    const handleHashChange = () => {
      setView(getInitialView())
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    window.addEventListener('hashchange', handleHashChange)
    return () => window.removeEventListener('hashchange', handleHashChange)
  }, [])

  const openGame = (event?: MouseEvent<HTMLAnchorElement | HTMLButtonElement>) => {
    event?.preventDefault()
    window.location.hash = 'play'
  }

  const backToLanding = () => {
    window.location.hash = ''
  }

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

  const handleSend = async () => {
    const trimmed = draft.trim()
    if (!trimmed || isStreaming) {
      return
    }

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
                    content: payload.message || '请求失败',
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
            }
          },
        },
      )
    } catch (error) {
      const failureMessage = error instanceof Error ? error.message : '连接现场失败'
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

  if (view === 'game') {
    return (
      <GameExperience
        messages={messages}
        progress={progress}
        draft={draft}
        isStreaming={isStreaming}
        onDraftChange={setDraft}
        onSend={handleSend}
        onBack={backToLanding}
        onPrompt={setDraft}
        characterSeats={characterSeats}
        quickPrompts={quickPrompts}
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
            <a href="#play" onClick={openGame}>
              进入游戏
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
                开始游戏
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
                  进入游戏
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
