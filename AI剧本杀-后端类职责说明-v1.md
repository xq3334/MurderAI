# AI剧本杀后端类职责说明 v1

本文对应当前根目录下 `ai-murder-backend` 的 Spring Boot + Spring AI MVP 后端实现。

## 1. 后端分层总览

- `web.controller`
  - 提供 HTTP 接口，负责接收请求、调用应用服务、返回 DTO。
- `web.dto`
  - 对外接口的数据载体，隔离内部领域模型。
- `application`
  - 业务编排层，负责“开局”“发言”“意图分析”“剧情调度”等核心流程。
- `domain`
  - 纯业务模型，定义剧本、角色、会话、消息、结局等核心概念。
- `repository`
  - 仓储接口，抽象会话存储和剧本加载。
- `infrastructure.repository`
  - 仓储接口的当前实现，MVP 阶段使用内存会话和 classpath 剧本。
- `ai`
  - AI 角色回复能力抽象与实现。
- `config`
  - Spring 配置、属性配置、CORS、AI Bean 装配。
- `support`
  - 通用支持工具。

## 2. 请求主链路

### 2.1 创建游戏会话

`GameSessionController` -> `GameSessionAppService#createSession` -> `ScriptRepository` 读取剧本 -> 初始化 `GameSession` -> 写入 `GameSessionRepository` -> 返回 `GameSessionResponse`

### 2.2 玩家发送消息

`ChatController` -> `ChatTurnAppService#handle`

内部步骤：

1. 读取会话
2. 校验会话是否已结束
3. 加载剧本
4. 记录玩家消息
5. `IntentAnalyzer` 分析玩家意图
6. `StoryDirector` 推进回合、触发事件、切换阶段、判断结局
7. `CharacterReplyGenerator` 为应答角色生成回复
8. 写回会话仓储
9. 返回 `ChatTurnResponse`

## 3. 每个类的作用

### 3.1 启动与配置

#### `AiMurderApplication`
- 项目启动入口。
- 启用 Spring Boot 自动配置。
- 启用 `AppProperties` 配置绑定。

#### `AppProperties`
- 读取 `application.yml` 中 `app.*` 配置。
- 当前承载的核心配置：
  - 默认剧本 ID
  - 最近消息窗口大小

#### `AiGeneratorConfig`
- 装配 `CharacterReplyGenerator` Bean。
- 若容器里存在 `ChatModel`，使用 `SpringAiCharacterReplyGenerator`。
- 若不存在，回退到 `StubCharacterReplyGenerator`，保证本地无模型时也能跑通流程。

#### `WebConfig`
- 配置 `/api/**` 的 CORS。
- 当前允许前端本地开发地址访问后端。

### 3.2 应用服务层

#### `GameSessionAppService`
- “开局”应用服务。
- 负责创建游戏会话、读取游戏会话、加载剧本。
- 把内部 `GameSession` 和 `Message` 转成对外 DTO。
- 是“会话生命周期管理”的主入口。

#### `ChatTurnAppService`
- “发一轮消息”的主编排服务。
- 负责把玩家发言完整走完一轮：
  - 记录玩家消息
  - 分析意图
  - 推进剧情
  - 生成角色回复
  - 判断并写入结局
  - 拼装返回前端的本轮结果
- 它是当前后端最核心的业务入口。

#### `IntentAnalyzer`
- 玩家输入意图分析器。
- 负责从自然语言里提取：
  - 发言类型
  - 是否为指控
  - 是否在索要线索
  - 指向了哪些角色
  - 命中的关键词
- 当前是规则版实现，适合 MVP。

#### `StoryDirector`
- 剧情导演器，负责“这一轮剧情怎么往前走”。
- 核心职责：
  - 回合数推进
  - 选择本轮应答角色
  - 按关键词触发剧本事件
  - 推进剧情阶段
  - 揭示线索
  - 判断是否进入结局
- 它相当于“局势控制器”。

### 3.3 领域模型层

#### `CharacterProfile`
- 角色静态档案模型。
- 描述角色固有信息：
  - 身份
  - 表面人设
  - 隐藏秘密
  - 性格
  - 说话风格
  - 目标
  - 知识范围
  - 人际关系

#### `ScriptDefinition`
- 剧本定义聚合根。
- 整体描述一个完整剧本的静态结构。
- 内部包含多个嵌套定义：
  - `OpeningDefinition`：开场设定
  - `StoryStageDefinition`：剧情阶段
  - `StoryEventDefinition`：可触发事件
  - `ClueDefinition`：线索定义
  - `EndingDefinition`：结局定义

#### `PlayerIntent`
- 玩家发言分析结果模型。
- 是 `IntentAnalyzer` 的输出，也是 `StoryDirector` 和 AI 回复生成器的重要输入。

#### `SessionModels`
- 会话领域模型集合类。
- 当前把一组强关联模型集中放在一个文件中，便于 MVP 快速收敛。

其中包含：

#### `SessionModels.SessionStatus`
- 会话状态枚举。
- 区分进行中与已结束。

#### `SessionModels.MessageType`
- 消息类型枚举。
- 区分系统消息、玩家消息、角色消息。

#### `SessionModels.EmotionState`
- 角色情绪状态枚举。
- 给角色回复生成提供情绪语境。

#### `SessionModels.EndingType`
- 结局类型枚举。
- 区分成功、失败、中性结局。

#### `SessionModels.IntentType`
- 玩家发言意图枚举。
- 当前包括提问、指控、试探、陈述、索要线索。

#### `SessionModels.Message`
- 单条消息模型。
- 表示一条在会话中流动的消息记录。

#### `SessionModels.EndingResult`
- 最终结局结果模型。
- 表示本局是否结束以及结束文案是什么。

#### `SessionModels.CharacterState`
- 角色动态状态模型。
- 描述角色在一局中的可变状态：
  - 当前情绪
  - 对玩家信任值
  - 怀疑映射
  - 已知线索
  - 当前关注点

#### `SessionModels.StoryState`
- 剧情动态状态模型。
- 描述一局中的故事推进状态：
  - 当前阶段 ID
  - 回合数
  - 已触发事件
  - 已揭示线索

#### `SessionModels.GameSession`
- 单局游戏会话聚合根。
- 把会话级核心状态收在一起：
  - 会话 ID
  - 剧本 ID
  - 会话状态
  - 创建时间
  - 剧情状态
  - 角色状态表
  - 消息列表
  - 结局结果
- 同时提供聚合内行为：
  - 追加消息
  - 揭示线索
  - 结束会话

### 3.4 AI 角色回复层

#### `CharacterReplyGenerator`
- 角色回复生成接口。
- 抽象“给某个角色生成一句符合身份的回复”这件事。
- 这样后续可以自由切换生成策略。

#### `SpringAiCharacterReplyGenerator`
- 基于 Spring AI `ChatModel` 的真实大模型回复实现。
- 负责拼接 system prompt / user prompt。
- 把剧本、角色、会话状态、玩家意图注入给模型，让输出尽量保持角色感。

#### `StubCharacterReplyGenerator`
- 无模型时的兜底实现。
- 按规则返回固定风格回复。
- 主要用于：
  - 本地开发
  - 无 API Key 环境
  - 接口联调
  - 逻辑测试

### 3.5 仓储层

#### `GameSessionRepository`
- 会话仓储接口。
- 抽象会话保存与读取能力。

#### `ScriptRepository`
- 剧本仓储接口。
- 抽象剧本定义的读取能力。

#### `InMemoryGameSessionRepository`
- `GameSessionRepository` 的内存实现。
- 当前使用 `ConcurrentHashMap` 保存会话。
- 适合 MVP 和单机调试，不适合生产持久化。

#### `ClasspathScriptRepository`
- `ScriptRepository` 的 classpath JSON 实现。
- 从 `resources/scripts/*.json` 读取剧本，并做缓存。
- 当前是无数据库方案下最适合的剧本加载方式。

### 3.6 Web 接口层

#### `GameSessionController`
- 会话相关接口控制器。
- 提供：
  - 创建会话
  - 查询会话

#### `ChatController`
- 聊天消息接口控制器。
- 提供：
  - 发送玩家消息
  - 获得本轮新状态

#### `ScriptController`
- 剧本配置接口控制器。
- 当前只提供默认剧本 ID 查询。
- 方便前端启动时知道默认进哪个剧本。

#### `ApiExceptionHandler`
- 全局异常处理器。
- 负责把常见异常统一转成 HTTP 状态码和简单错误消息。

### 3.7 DTO 层

#### `CreateSessionRequest`
- 创建会话请求 DTO。

#### `SendMessageRequest`
- 发送消息请求 DTO。
- 目前校验消息内容非空。

#### `GameSessionResponse`
- 创建会话 / 查询会话返回 DTO。
- 返回会话基本信息、角色列表、当前阶段、开场消息等。

#### `ChatTurnResponse`
- 单轮对话返回 DTO。
- 返回当前阶段、是否结束、结局信息、最近消息窗口、已揭示线索。

#### `CharacterSummaryResponse`
- 角色摘要 DTO。
- 给前端角色列表用，避免直接暴露完整角色秘密。

#### `MessageResponse`
- 消息 DTO。
- 给前端渲染聊天流使用。

#### `EndingResponse`
- 结局 DTO。
- 用于前端展示结局面板。

### 3.8 支撑类

#### `IdGenerator`
- 简单 ID 生成器。
- 负责生成 `session-*`、`msg-*` 这类带前缀的短 ID。

## 4. 当前后端逻辑是否顺

整体上，这个 MVP 后端主链路是顺的：

1. 剧本定义和会话状态已经分开
2. 会话创建、剧情推进、消息生成职责分层清晰
3. AI 回复层已经做了接口抽象，后续替换模型实现成本低
4. 当前即使没有接真实大模型，也可以靠 stub 把流程走通

## 5. 我这次重点修正的逻辑

### 5.1 结局判断不再只靠硬编码文案

现在 `StoryDirector` 会：

- 优先使用 `IntentAnalyzer` 已经识别出的 `accusation` 结果
- 结合目标角色或文本提及判断是否在指认真凶
- 优先映射剧本 JSON 里的 `endings` 配置

这样做的好处：

- 应用逻辑与剧本配置更一致
- 后续换剧本时，不容易出现代码和 JSON 文案对不上的问题

### 5.2 补强了结局测试覆盖

新增了两类验证：

- 证据足够且指认真凶时，应该走成功结局
- 证据不足时提前指认，应该走失败结局

## 6. 当前仍然存在的边界与后续建议

### 6.1 会话存储还是内存态

- 当前服务重启后会话会丢失
- 多实例部署也无法共享会话

后续建议：
- 先上 MySQL / PostgreSQL 持久化会话
- 如果后面要做长期记忆，再考虑 Redis + 数据库双层

### 6.2 意图识别还是规则版

- 现在适合 MVP
- 但遇到复杂自由表达时，识别精度会有限

后续建议：
- 可以先保留规则版兜底
- 再叠一层 LLM 意图分类

### 6.3 角色状态更新还比较轻

- 目前主要更新信任值、情绪、关注点
- 还没有把“谁撒谎了、谁被逼急了、谁开始转移话题”做成更丰富的状态机

后续建议：
- 把 `CharacterState` 再扩展成更细的行为状态模型

### 6.4 剧情导演还偏关键词驱动

- 当前事件触发依赖 `triggerKeywords`
- 适合规则明确的 MVP

后续建议：
- 后面可以让导演层 Agent 根据“事件条件 + 当前局势”做更柔性的调度

## 7. 一句话结论

当前后端作为 MVP 骨架是成立的，主流程没有跑偏；它最大的优点是分层清楚、替换 AI 能力方便。下一阶段最该继续加强的是：

- 会话持久化
- 更强的意图识别
- 更细的角色状态机
- 更智能的剧情导演逻辑
