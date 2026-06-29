# AI 剧本杀 MVP 后端模块拆分与类设计 v1

## 1. 文档目标

本文档用于明确 AI 剧本杀 MVP 的后端模块拆分、职责边界和核心类设计，作为编码阶段的直接参考。

当前前提：

- JDK 17
- Spring Boot
- Spring AI
- 单体架构
- 暂不接数据库
- 会话状态保存在内存中

---

## 2. 包结构建议

建议采用按职责分层的包结构，而不是把所有内容堆进 controller/service/entity。

可参考如下结构：

```text
com.example.aimurder
  ├─ web
  │  ├─ controller
  │  ├─ dto
  │  └─ advice
  ├─ application
  │  ├─ service
  │  ├─ orchestrator
  │  └─ mapper
  ├─ domain
  │  ├─ session
  │  ├─ script
  │  ├─ character
  │  ├─ story
  │  ├─ clue
  │  └─ common
  ├─ ai
  │  ├─ gateway
  │  ├─ prompt
  │  ├─ parser
  │  └─ summarizer
  ├─ infrastructure
  │  ├─ repository
  │  ├─ loader
  │  ├─ config
  │  └─ support
  └─ shared
     ├─ enums
     ├─ exception
     └─ util
```

---

## 3. 模块拆分

## 3.1 Web 层

职责：

1. 提供 REST 接口
2. 接收和返回 DTO
3. 参数校验
4. 统一异常处理

不负责：

1. 直接操作会话状态
2. 直接拼 Prompt
3. 直接调用模型

### 主要类建议

#### `GameSessionController`

职责：

1. 创建新会话
2. 查询会话状态
3. 结束会话

建议接口：

- `createSession()`
- `getSession(sessionId)`
- `finishSession(sessionId)`

#### `ChatController`

职责：

1. 接收玩家消息
2. 返回一轮处理结果
3. 查询消息历史

建议接口：

- `sendMessage(sessionId, request)`
- `listMessages(sessionId)`

#### `GlobalExceptionHandler`

职责：

1. 统一处理业务异常
2. 转换为标准错误响应

---

## 3.2 Application 层

职责：

1. 编排完整业务流程
2. 协调领域对象与 AI 组件
3. 输出给 Web 层可用的结果

Application 层是 MVP 的主控层。

### 主要类建议

#### `GameSessionAppService`

职责：

1. 创建游戏会话
2. 初始化角色状态
3. 初始化剧情状态
4. 生成开场消息

核心方法：

```java
GameSessionView createSession(CreateSessionCommand command);
GameSessionView getSession(String sessionId);
void finishSession(String sessionId);
```

#### `ChatTurnAppService`

职责：

1. 承接一轮玩家输入
2. 调用输入分析
3. 驱动剧情调度
4. 触发角色回复
5. 汇总本轮输出

核心方法：

```java
ChatTurnResult handlePlayerTurn(String sessionId, PlayerInputCommand command);
```

#### `SessionQueryService`

职责：

1. 查询会话详情
2. 查询消息历史
3. 查询当前公开线索

#### `StoryOrchestrationService`

职责：

1. 协调剧情调度器、角色响应器、状态更新器
2. 作为聊天主链路的编排中心

---

## 3.3 Domain 层

Domain 层承载系统核心业务概念和规则。

### 3.3.1 session 子域

#### `GameSession`

职责：

1. 表示一局游戏
2. 聚合本局角色状态、剧情状态、消息历史
3. 提供受控的状态变更方法

关键字段建议：

```java
public class GameSession {
    private String sessionId;
    private String scriptId;
    private SessionStatus status;
    private Instant createdAt;
    private StoryState storyState;
    private Map<String, CharacterState> characterStates;
    private List<Message> messages;
    private List<StoryEventRecord> eventRecords;
    private Set<String> revealedClueIds;
    private ConversationSummary conversationSummary;
}
```

关键行为建议：

1. `appendMessage(Message message)`
2. `appendMessages(List<Message> messages)`
3. `markFinished(EndingResult endingResult)`
4. `revealClue(String clueId)`
5. `recordEvent(StoryEventRecord record)`
6. `updateCharacterState(String characterId, CharacterState state)`

#### `SessionStatus`

枚举建议：

- `ACTIVE`
- `FINISHED`
- `FAILED`

#### `ConversationSummary`

职责：

1. 保存历史对话摘要
2. 为上下文压缩提供结构化载体

---

### 3.3.2 script 子域

#### `ScriptDefinition`

职责：

1. 表示剧本静态配置
2. 聚合角色、阶段、事件、线索、结局定义

关键字段建议：

```java
public class ScriptDefinition {
    private String scriptId;
    private String title;
    private String premise;
    private OpeningDefinition opening;
    private List<CharacterProfile> characters;
    private List<StoryStageDefinition> stages;
    private List<StoryEventDefinition> events;
    private List<ClueDefinition> clues;
    private List<EndingDefinition> endings;
}
```

#### `OpeningDefinition`

职责：

1. 定义开场旁白
2. 定义初始阶段
3. 定义首轮公开信息

#### `StoryStageDefinition`

职责：

1. 定义每个阶段的目标和限制
2. 指定推荐活跃角色
3. 指定可触发事件范围

#### `StoryEventDefinition`

职责：

1. 定义事件触发条件
2. 定义事件效果
3. 定义是否影响角色状态或公开线索

#### `EndingDefinition`

职责：

1. 定义结局触发条件
2. 定义结局类型和描述模板

---

### 3.3.3 character 子域

#### `CharacterProfile`

职责：

1. 描述角色静态设定
2. 为 Prompt 提供静态素材

关键字段建议：

```java
public class CharacterProfile {
    private String characterId;
    private String name;
    private String identity;
    private String publicPersona;
    private String hiddenSecret;
    private List<String> personalityTraits;
    private String speakingStyle;
    private List<String> goals;
    private List<String> publicKnowledge;
    private List<String> privateKnowledge;
    private Map<String, String> relationships;
}
```

#### `CharacterState`

职责：

1. 表示角色动态状态
2. 在每轮对话后更新

关键字段建议：

```java
public class CharacterState {
    private String characterId;
    private EmotionState emotionState;
    private int trustToPlayer;
    private Map<String, Integer> suspicionMap;
    private Set<String> revealedSecrets;
    private Set<String> knownClueIds;
    private String currentFocus;
    private boolean activeInCurrentStage;
}
```

关键行为建议：

1. `adjustTrustToPlayer(int delta)`
2. `increaseSuspicion(String targetId, int delta)`
3. `revealSecret(String secretKey)`
4. `rememberClue(String clueId)`
5. `changeFocus(String focus)`

#### `EmotionState`

枚举建议：

- `CALM`
- `DEFENSIVE`
- `ANGRY`
- `NERVOUS`
- `CONFIDENT`

---

### 3.3.4 story 子域

#### `StoryState`

职责：

1. 保存当前剧情主状态
2. 作为剧情调度器判断依据

关键字段建议：

```java
public class StoryState {
    private String currentStageId;
    private int turnCount;
    private Set<String> triggeredEventIds;
    private Set<String> availableClueIds;
    private Set<String> revealedClueIds;
    private String dominantConflict;
    private EndingProgress endingProgress;
}
```

关键行为建议：

1. `advanceTurn()`
2. `switchStage(String stageId)`
3. `triggerEvent(String eventId)`
4. `revealClue(String clueId)`

#### `StoryEventRecord`

职责：

1. 保存已发生事件日志
2. 支撑后续摘要、调试和前端展示

#### `EndingProgress`

职责：

1. 保存结局相关进度
2. 标记是否已满足某些结局前置条件

#### `EndingResult`

职责：

1. 表示最终结局

关键字段建议：

```java
public class EndingResult {
    private String endingId;
    private EndingType type;
    private String title;
    private String summary;
}
```

#### `EndingType`

枚举建议：

- `SUCCESS`
- `FAILURE`
- `NEUTRAL`

---

### 3.3.5 clue 子域

#### `ClueDefinition`

职责：

1. 定义线索静态内容
2. 说明线索的曝光条件和影响

#### `ClueExposureRule`

职责：

1. 判断线索是否可公开

---

## 4. AI 层设计

AI 层不直接暴露给 Controller，而是由 Application 层调用。

### 4.1 `ModelGateway`

职责：

1. 统一封装 Spring AI 调用
2. 屏蔽具体模型实现细节
3. 统一超时、重试、日志

建议方法：

```java
String generateText(ModelPrompt prompt);
```

### 4.2 `IntentAnalysisService`

职责：

1. 分析玩家输入意图
2. 提取发言对象、语气、行动类型、怀疑对象

建议输出对象：

#### `PlayerIntent`

字段建议：

```java
public class PlayerIntent {
    private IntentType intentType;
    private List<String> targetCharacterIds;
    private List<String> mentionedClueIds;
    private boolean accusation;
    private boolean asksForEvidence;
    private String normalizedText;
}
```

#### `IntentType`

枚举建议：

- `QUESTION`
- `ACCUSATION`
- `DEFENSE`
- `PROBE`
- `STATEMENT`
- `REQUEST_CLUE`

### 4.3 `CharacterPromptBuilder`

职责：

1. 为单个角色拼装 Prompt
2. 注入静态设定和当前状态
3. 注入本轮玩家输入和上下文摘要

建议方法：

```java
ModelPrompt buildCharacterPrompt(
    ScriptDefinition script,
    CharacterProfile profile,
    CharacterState state,
    StoryState storyState,
    PlayerIntent playerIntent,
    List<Message> recentMessages,
    ConversationSummary summary
);
```

### 4.4 `CharacterResponseGenerator`

职责：

1. 基于角色 Prompt 生成回复
2. 将文本包装为角色消息对象

建议方法：

```java
CharacterReply generateReply(CharacterReplyCommand command);
```

### 4.5 `ResponseValidationService`

职责：

1. 过滤越权信息
2. 检查是否泄露不该公开的真相
3. 检查是否偏离角色风格

MVP 阶段可以先用规则校验，后续再增强。

### 4.6 `ConversationSummaryService`

职责：

1. 对长对话做摘要
2. 压缩旧上下文
3. 保留重要事实和关系变化

---

## 5. Application 编排组件设计

### 5.1 `PlayerTurnProcessor`

职责：

1. 处理玩家一轮输入
2. 串联意图分析、剧情调度、角色响应、状态更新

建议主方法：

```java
ChatTurnResult process(GameSession session, PlayerInputCommand command);
```

### 5.2 `StoryDirector`

职责：

1. 决定谁该发言
2. 决定事件触发
3. 决定阶段推进
4. 决定结局是否成立

建议方法：

```java
DirectionDecision decideNextStep(
    GameSession session,
    PlayerIntent playerIntent
);
```

#### `DirectionDecision`

字段建议：

```java
public class DirectionDecision {
    private List<String> responderCharacterIds;
    private List<String> triggeredEventIds;
    private String nextStageId;
    private boolean endingReached;
    private String endingId;
    private List<SystemNotice> systemNotices;
}
```

### 5.3 `CharacterStateUpdater`

职责：

1. 根据玩家输入与角色输出更新状态
2. 更新信任、怀疑、情绪、焦点

### 5.4 `StoryStateUpdater`

职责：

1. 更新回合数
2. 更新阶段
3. 更新已触发事件和线索暴露

### 5.5 `TurnResultAssembler`

职责：

1. 汇总玩家消息、系统消息、角色消息
2. 输出给前端一轮完整结果

---

## 6. Infrastructure 层设计

## 6.1 Repository 抽象

即使当前不用数据库，也建议先定义接口。

### `GameSessionRepository`

```java
public interface GameSessionRepository {
    void save(GameSession session);
    Optional<GameSession> findById(String sessionId);
    void deleteById(String sessionId);
}
```

### `ScriptRepository`

```java
public interface ScriptRepository {
    Optional<ScriptDefinition> findById(String scriptId);
    List<ScriptDefinition> findAll();
}
```

## 6.2 内存实现

### `InMemoryGameSessionRepository`

职责：

1. 用 ConcurrentHashMap 保存会话
2. 提供线程安全读写

### `ClasspathScriptRepository`

职责：

1. 从 `resources/scripts` 读取剧本文件
2. 解析 JSON 或 YAML

## 6.3 支撑组件

### `IdGenerator`

职责：

1. 生成 sessionId、messageId、eventId

### `TimeProvider`

职责：

1. 统一提供当前时间，方便测试

### `JsonMapper`

职责：

1. 封装 Jackson 读写

---

## 7. DTO 设计建议

## 7.1 请求 DTO

### `CreateSessionRequest`

```java
public class CreateSessionRequest {
    private String scriptId;
}
```

### `SendMessageRequest`

```java
public class SendMessageRequest {
    private String content;
    private String targetCharacterId;
}
```

## 7.2 响应 DTO

### `GameSessionResponse`

```java
public class GameSessionResponse {
    private String sessionId;
    private String scriptId;
    private String scriptTitle;
    private String status;
    private String currentStage;
    private List<CharacterSummaryResponse> characters;
    private List<MessageResponse> openingMessages;
}
```

### `ChatTurnResponse`

```java
public class ChatTurnResponse {
    private String sessionId;
    private String currentStage;
    private boolean finished;
    private EndingResponse ending;
    private List<MessageResponse> messages;
    private List<String> revealedClues;
}
```

### `MessageResponse`

```java
public class MessageResponse {
    private String messageId;
    private String speakerId;
    private String speakerName;
    private String type;
    private String content;
    private Instant timestamp;
}
```

---

## 8. 命令对象建议

为了避免 Controller 直接传 DTO 到领域逻辑，建议引入命令对象。

### `CreateSessionCommand`

```java
public record CreateSessionCommand(String scriptId) {
}
```

### `PlayerInputCommand`

```java
public record PlayerInputCommand(
    String content,
    String targetCharacterId
) {
}
```

### `CharacterReplyCommand`

```java
public record CharacterReplyCommand(
    GameSession session,
    CharacterProfile profile,
    CharacterState state,
    PlayerIntent playerIntent
) {
}
```

---

## 9. 消息模型设计

### `Message`

建议作为统一消息模型，覆盖玩家、角色、系统三类输出。

```java
public class Message {
    private String messageId;
    private MessageType type;
    private String speakerId;
    private String speakerName;
    private String targetId;
    private String content;
    private Instant timestamp;
}
```

### `MessageType`

枚举建议：

- `PLAYER`
- `CHARACTER`
- `SYSTEM`
- `NARRATION`

---

## 10. 一轮对话时序建议

以下是推荐时序：

1. `ChatController` 接收请求
2. `ChatTurnAppService` 加载会话
3. 生成玩家消息并写入 `GameSession`
4. `IntentAnalysisService` 解析玩家输入
5. `StoryDirector` 判断回应者、事件、阶段变化
6. 对每个回应角色执行 `CharacterPromptBuilder`
7. `CharacterResponseGenerator` 调用 `ModelGateway`
8. `ResponseValidationService` 校验输出
9. 写入角色消息
10. `CharacterStateUpdater` 和 `StoryStateUpdater` 更新状态
11. 必要时 `ConversationSummaryService` 更新摘要
12. `TurnResultAssembler` 组装响应

---

## 11. 配置文件建议

## 11.1 application.yml

建议配置项：

```yaml
app:
  game:
    session-timeout-minutes: 120
    recent-message-window: 8
    summary-trigger-turns: 6
  ai:
    max-retries: 2
    timeout-seconds: 30
    temperature: 0.8
```

## 11.2 剧本文件

建议每个剧本一个独立文件，例如：

```text
resources/scripts/mansion-mystery.json
```

## 11.3 Prompt 文件

建议拆分：

```text
resources/prompts/system/global-system.txt
resources/prompts/character/character-reply.txt
resources/prompts/story/intent-analysis.txt
resources/prompts/story/conversation-summary.txt
```

---

## 12. 单元测试优先级建议

MVP 阶段最值得先测的是规则层，不是模型输出文本本身。

### 第一优先级

1. `StoryDirector` 阶段推进判断
2. `CharacterStateUpdater` 状态变化规则
3. `StoryStateUpdater` 事件与线索更新
4. `InMemoryGameSessionRepository` 会话读写

### 第二优先级

1. `TurnResultAssembler`
2. `ScriptRepository` 配置加载
3. `PromptBuilder` 关键字段注入完整性

### 第三优先级

1. Web 层接口测试
2. Spring AI 调用适配测试

---

## 13. 首批可直接创建的类清单

如果你准备开始编码，建议第一批先建这些类。

### 领域模型

1. `GameSession`
2. `StoryState`
3. `CharacterProfile`
4. `CharacterState`
5. `ScriptDefinition`
6. `Message`
7. `EndingResult`

### 枚举

1. `SessionStatus`
2. `MessageType`
3. `IntentType`
4. `EndingType`
5. `EmotionState`

### Repository

1. `GameSessionRepository`
2. `ScriptRepository`
3. `InMemoryGameSessionRepository`
4. `ClasspathScriptRepository`

### 应用服务

1. `GameSessionAppService`
2. `ChatTurnAppService`
3. `StoryOrchestrationService`

### AI 服务

1. `ModelGateway`
2. `IntentAnalysisService`
3. `CharacterPromptBuilder`
4. `CharacterResponseGenerator`
5. `ConversationSummaryService`

### Web

1. `GameSessionController`
2. `ChatController`
3. `GlobalExceptionHandler`

---

## 14. 当前设计结论

这套模块划分的重点，是先把系统分成四块清晰的责任边界：

1. Web 只管收发
2. Application 负责编排
3. Domain 负责状态和规则
4. AI 层负责模型交互

这样做的好处是：

1. 现在不用数据库也能顺利开发
2. 后续切数据库时改动面可控
3. 后续加 RAG、向量检索、存档时不会推翻现有结构
4. 业务规则不会被 Prompt 逻辑淹没

对你的 MVP 来说，这已经是一套足够稳、也足够能开工的后端拆分方案。
