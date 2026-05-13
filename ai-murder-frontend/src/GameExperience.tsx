import { useEffect, useMemo, useRef, useState } from 'react'
import type { ChatStreamProgressResponse, EndingRevealResponse } from './api'

type ChatMessage = {
  id: string
  speaker: string
  speakerKey: string
  role: 'system' | 'player' | 'character' | 'narrator' | 'pending'
  kind: 'opening' | 'dialogue' | 'clue' | 'scene'
  tone?: string
  content: string
}

type GameExperienceProps = {
  messages: ChatMessage[]
  progress: ChatStreamProgressResponse | null
  draft: string
  isStreaming: boolean
  onDraftChange: (value: string) => void
  onSend: () => void
  onBack: () => void
  characterSeats: Array<{
    name: string
    role: string
    mood: string
    status: string
  }>
  canAccuse: boolean
  accusationOptions: Array<{
    value: string
    label: string
    description: string
  }>
  accusedCharacterId: string
  onAccusedCharacterChange: (value: string) => void
  reasoning: string
  onReasoningChange: (value: string) => void
  onAccuse: () => void
  isAccusing: boolean
  ending: EndingRevealResponse | null
  hints: string[]
  quickActions: string[]
  isHintsOpen: boolean
  isHintsLoading: boolean
  hintError: string
  onToggleHints: () => void
  onUseHint: (hint: string) => void
  onUseQuickAction: (action: string) => void
}

type HintTheme = {
  key: 'storm' | 'romance' | 'manor' | 'neutral'
  eyebrow: string
  title: string
  accent: string
  frame: string
}

const roleMeta: Record<ChatMessage['role'], { badge: string }> = {
  system: { badge: '控场' },
  player: { badge: '玩家' },
  character: { badge: '角色' },
  narrator: { badge: '旁白' },
  pending: { badge: '整理中' },
}

function resolveHintTheme(progress: ChatStreamProgressResponse | null): HintTheme {
  const scriptName = progress?.scriptName ?? ''
  const stageName = progress?.currentStageName ?? ''
  const objective = progress?.objective ?? ''
  const source = `${scriptName} ${stageName} ${objective}`.toLowerCase()

  if (
    scriptName.includes('蝉鸣') ||
    stageName.includes('晚自习') ||
    objective.includes('关系') ||
    source.includes('romance')
  ) {
    return {
      key: 'romance',
      eyebrow: 'Pulse Trace',
      title: '暧昧流向',
      accent: '从情绪、回避和立场试探入手。',
      frame: '让提示像被折起的秘密便签。',
    }
  }

  if (
    scriptName.includes('山庄') ||
    scriptName.includes('庄园') ||
    objective.includes('宅邸') ||
    source.includes('manor') ||
    source.includes('mansion')
  ) {
    return {
      key: 'manor',
      eyebrow: 'House Echo',
      title: '宅邸回声',
      accent: '从空间、目击和时间裂缝切入。',
      frame: '像从旧宅档案柜里抽出的调查卡。',
    }
  }

  if (
    scriptName.includes('雨夜') ||
    stageName.includes('停电') ||
    objective.includes('暴雨') ||
    source.includes('storm')
  ) {
    return {
      key: 'storm',
      eyebrow: 'Storm Signal',
      title: '风暴想法',
      accent: '沿着停电前后、气氛异动和遮掩点推进。',
      frame: '像雨夜舞台上被灯光照亮的一组线索卡。',
    }
  }

  return {
    key: 'neutral',
    eyebrow: 'Scene Thread',
    title: '推进想法',
    accent: '优先抓住当前对话里最松动的地方。',
    frame: '保持中性侦查感，适配后续更多副本风格。',
  }
}

export function GameExperience({
  messages,
  progress,
  draft,
  isStreaming,
  onDraftChange,
  onSend,
  onBack,
  characterSeats,
  canAccuse,
  accusationOptions,
  accusedCharacterId,
  onAccusedCharacterChange,
  reasoning,
  onReasoningChange,
  onAccuse,
  isAccusing,
  ending,
  hints,
  quickActions,
  isHintsOpen,
  isHintsLoading,
  hintError,
  onToggleHints,
  onUseHint,
  onUseQuickAction,
}: GameExperienceProps) {
  const transcriptRef = useRef<HTMLDivElement | null>(null)
  const shouldAutoScrollRef = useRef(true)
  const [isAccusationOpen, setIsAccusationOpen] = useState(false)

  const stageProgressLabel = useMemo(() => {
    if (!progress) {
      return '1/3 · 暴雨封庄'
    }
    return `${progress.currentStageOrder}/${progress.totalStages} · ${progress.currentStageName}`
  }, [progress])

  const hintTheme = useMemo(() => resolveHintTheme(progress), [progress])

  useEffect(() => {
    const container = transcriptRef.current
    if (!container || !shouldAutoScrollRef.current) {
      return
    }

    container.scrollTo({
      top: container.scrollHeight,
      behavior: 'smooth',
    })
  }, [messages])

  useEffect(() => {
    if (!canAccuse) {
      setIsAccusationOpen(false)
    }
  }, [canAccuse])

  useEffect(() => {
    if (ending) {
      setIsAccusationOpen(false)
    }
  }, [ending])

  const handleComposerSend = () => {
    shouldAutoScrollRef.current = true
    onSend()
  }

  return (
    <div className="game-shell">
      <div className="game-shell__curtain game-shell__curtain--left" />
      <div className="game-shell__curtain game-shell__curtain--right" />
      <div className="game-shell__curtain-glow" />
      <div className="game-shell__veil game-shell__veil--left" />
      <div className="game-shell__veil game-shell__veil--right" />
      <header className="game-topbar">
        <button type="button" className="game-topbar__back" onClick={onBack}>
          返回首页
        </button>
        <div className="game-topbar__title">
          <span>Night Session</span>
          <strong>{progress?.scriptName ?? '夜幕疑局'}</strong>
        </div>
        <div className="game-topbar__status">
          <span>当前状态</span>
          <strong>{isStreaming ? '多人讨论进行中' : '等待你的发问'}</strong>
        </div>
      </header>

      <main className="game-layout">
        <aside className="game-sidebar">
          <section className="game-panel game-panel--intro">
            <span className="game-panel__eyebrow">Case Brief</span>
            <h2>{progress?.currentStageName ?? '雨夜断灯'}</h2>
            <p>{progress?.objective ?? '你是今夜被临时留下的中立调查者。先稳住现场，再推动第一轮口供比对。'}</p>
          </section>

          <section className="game-panel game-panel--progress">
            <div className="game-panel__heading">
              <span className="game-panel__eyebrow">Session Progress</span>
              <strong>调查进度</strong>
            </div>
            <div className="progress-stack">
              <div className="progress-chip">
                <span>玩家身份</span>
                <strong>{progress?.playerRoleName ?? '受邀旁观调查者'}</strong>
              </div>
              <div className="progress-chip">
                <span>当前阶段</span>
                <strong>{stageProgressLabel}</strong>
              </div>
            </div>
          </section>

          <section className="game-panel">
            <div className="game-panel__heading">
              <span className="game-panel__eyebrow">Revealed Clues</span>
              <strong>已公开线索</strong>
            </div>
            <div className="clue-list">
              {progress?.revealedClues?.length ? (
                progress.revealedClues.map((clue) => (
                  <article key={clue.clueId} className={`clue-card ${clue.keyClue ? 'clue-card--key' : ''}`}>
                    <div className="clue-card__meta">
                      <strong>{clue.clueName}</strong>
                      <span>{clue.keyClue ? '关键' : '公开'}</span>
                    </div>
                    <p>{clue.content}</p>
                  </article>
                ))
              ) : (
                <div className="clue-empty">线索尚未正式公开。先从他们的语气、停顿和互相回避的细节里找裂缝。</div>
              )}
            </div>
          </section>

          <section className="game-panel">
            <div className="game-panel__heading">
              <span className="game-panel__eyebrow">Character Seats</span>
              <strong>角色席位</strong>
            </div>
            <div className="seat-list">
              {characterSeats.map((seat) => (
                <article key={seat.name} className="seat-card">
                  <div className="seat-card__meta">
                    <strong>{seat.name}</strong>
                    <span>{seat.role}</span>
                  </div>
                  <p>{seat.mood}</p>
                  <em>{seat.status}</em>
                </article>
              ))}
            </div>
          </section>
        </aside>

        <section className="game-stage">
          <div className="game-stage__header">
            <div>
              <span className="game-panel__eyebrow">Live Dialogue</span>
              <h1>对话主舞台</h1>
            </div>
            <div className="game-stage__chips">
              <button
                type="button"
                className={`game-stage__hint-toggle ${isHintsOpen ? 'game-stage__hint-toggle--active' : ''}`}
                onClick={onToggleHints}
                disabled={isHintsLoading}
              >
                {isHintsLoading ? '提示生成中...' : '想法提示'}
              </button>
            </div>
          </div>

          <div
            ref={transcriptRef}
            className="game-transcript"
            onScroll={(event) => {
              const container = event.currentTarget
              const distanceToBottom = container.scrollHeight - container.scrollTop - container.clientHeight
              shouldAutoScrollRef.current = distanceToBottom < 48
            }}
          >
            {messages.map((message) => (
              <article
                key={message.id}
                className={`game-message game-message--${message.role} game-message--${message.kind} game-message--speaker-${message.speakerKey}`}
              >
                <div className="game-message__label">
                  <strong>{message.speaker}</strong>
                  <span>{roleMeta[message.role].badge}</span>
                  {message.tone ? <em>{message.tone}</em> : null}
                </div>
                {message.kind === 'clue' ? (
                  <div className="game-message__clue-card">
                    <p>{message.content}</p>
                  </div>
                ) : (
                  <p>{message.content}</p>
                )}
              </article>
            ))}
          </div>

          <div className="game-composer">
            <div className="game-composer__box">
              {quickActions.length ? (
                <div className="game-quick-actions">
                  <span className="game-quick-actions__eyebrow">Scene Actions</span>
                  <div className="game-quick-actions__list">
                    {quickActions.map((action) => (
                      <button
                        key={action}
                        type="button"
                        className="game-quick-actions__chip"
                        onClick={() => onUseQuickAction(action)}
                        disabled={isStreaming}
                      >
                        {action}
                      </button>
                    ))}
                  </div>
                </div>
              ) : null}
              <textarea
                value={draft}
                onChange={(event) => onDraftChange(event.target.value)}
                placeholder="输入你的发问。你可以点名角色，也可以直接追问停电、遗嘱、账本或行迹。"
                rows={3}
                disabled={isStreaming}
                onKeyDown={(event) => {
                  if (event.key === 'Enter' && !event.shiftKey) {
                    event.preventDefault()
                    handleComposerSend()
                  }
                }}
              />
              <div className="game-composer__actions">
                <span>
                  {isStreaming ? '现场正在持续落下新发言与线索。' : '阶段目标和已公开线索会固定显示在左侧，不必来回翻记录。'}
                </span>
                <button type="button" className="game-send" onClick={handleComposerSend} disabled={isStreaming}>
                  {isStreaming ? '调查进行中...' : '发起追问'}
                </button>
              </div>
            </div>
          </div>
        </section>

        <aside className="game-rail">
          <div className="ending-panel">
            <p className="ending-panel__hint">
              {canAccuse
                ? '你已经进入最终阶段。现在可以提交一次正式指认，系统会直接进入结案。'
                : '最终指认会在最后阶段解锁。平时的怀疑和试探只会影响调查，不会直接结案。'}
            </p>
            <div className="ending-panel__status">
              {canAccuse ? '最终指认已解锁。请选择目标并确认。' : '尚未到最终指认阶段。继续追问，推进局势与关键线索。'}
            </div>
            <button
              type="button"
              className="ending-panel__submit"
              onClick={() => setIsAccusationOpen((current) => !current)}
              disabled={!canAccuse || !accusationOptions.length || isAccusing}
            >
              {isAccusationOpen ? '收起最终指认' : '打开最终指认'}
            </button>

            {isAccusationOpen ? (
              <>
                <div className="suspect-grid">
                  {accusationOptions.map((option) => (
                    <button
                      key={option.value}
                      type="button"
                      className={`suspect-chip ${accusedCharacterId === option.value ? 'suspect-chip--active' : ''}`}
                      onClick={() => onAccusedCharacterChange(option.value)}
                      disabled={isAccusing}
                    >
                      <strong>{option.label}</strong>
                      <span>{option.description}</span>
                    </button>
                  ))}
                </div>
                <textarea
                  className="ending-panel__textarea"
                  value={reasoning}
                  onChange={(event) => onReasoningChange(event.target.value)}
                  placeholder="可选填写一句推理理由，作为你的最终结案陈述。"
                  disabled={isAccusing}
                />
                <button
                  type="button"
                  className="ending-panel__submit"
                  onClick={onAccuse}
                  disabled={!accusedCharacterId || isAccusing}
                >
                  {isAccusing ? '正在提交指认...' : '确认最终指认'}
                </button>
              </>
            ) : null}
          </div>
        </aside>
      </main>

      <div className={`ending-reveal ${ending ? 'ending-reveal--open' : ''}`}>
        {ending ? (
          <>
            <div className="ending-reveal__backdrop" />
            <div className="ending-reveal__shell">
              <div className="ending-reveal__orbit ending-reveal__orbit--a" />
              <div className="ending-reveal__orbit ending-reveal__orbit--b" />
              <div className="ending-reveal__orbit ending-reveal__orbit--c" />
              <div className="ending-reveal__card">
                <div className="ending-reveal__face ending-reveal__face--front">
                  <span>Case Closed</span>
                  <strong>{ending.endingTitle}</strong>
                  <p>{ending.verdict}</p>
                </div>
                <div className="ending-reveal__face ending-reveal__face--back">
                  <span>Truth Reveal</span>
                  <strong>{ending.success ? '指认成立' : '指认落空'}</strong>
                  <p className="ending-reveal__verdict">{ending.playerOutcome}</p>
                  <div className="ending-reveal__meta">
                    <div>
                      <span>你指认的人</span>
                      <strong>{ending.accusedCharacterName}</strong>
                    </div>
                    <div>
                      <span>真正的凶手</span>
                      <strong>{ending.killerCharacterName}</strong>
                    </div>
                  </div>
                  <div className="ending-reveal__truth">
                    <p>{ending.truthStory}</p>
                  </div>
                  <div className="ending-reveal__evidence">
                    <span>Key Evidence</span>
                    <ul>
                      {ending.keyEvidence.map((item) => (
                        <li key={item}>{item}</li>
                      ))}
                    </ul>
                  </div>
                  <div className="ending-reveal__footer">
                    <p>{ending.reasoningSummary}</p>
                    <button type="button" className="ending-reveal__button" onClick={onBack}>
                      返回首页
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </>
        ) : null}
      </div>

      {isHintsOpen ? (
        <div className="hint-overlay" role="dialog" aria-modal="true" aria-label="想法提示">
          <button type="button" className="hint-overlay__backdrop" onClick={onToggleHints} aria-label="关闭想法提示" />
          <div className={`hint-overlay__panel hint-overlay__panel--${hintTheme.key}`}>
            <div className="hint-overlay__header">
              <div>
                <span className="hint-overlay__eyebrow">{hintTheme.eyebrow}</span>
                <h2>{hintTheme.title}</h2>
              </div>
              <div className="hint-overlay__actions">
                <p>{hintTheme.accent}</p>
                <button type="button" className="hint-overlay__back" onClick={onToggleHints}>
                  返回
                </button>
              </div>
            </div>

            {hintError ? <div className="hint-overlay__error">{hintError}</div> : null}

            {hints.length ? (
              <div className="hint-card-grid">
                {hints.map((hint, index) => (
                  <button
                    key={hint}
                    type="button"
                    className={`hint-card hint-card--${hintTheme.key}`}
                    style={{ ['--hint-delay' as string]: `${index * 70}ms` }}
                    onClick={() => onUseHint(hint)}
                  >
                    <span className="hint-card__index">{`0${index + 1}`}</span>
                    <strong>{hintTheme.frame}</strong>
                    <p>{hint}</p>
                    <span className="hint-card__cta">填入输入框</span>
                  </button>
                ))}
              </div>
            ) : !hintError && !isHintsLoading ? (
              <div className="hint-overlay__empty">还没有可用提示，继续推进对话后再试。</div>
            ) : null}
          </div>
        </div>
      ) : null}
    </div>
  )
}
