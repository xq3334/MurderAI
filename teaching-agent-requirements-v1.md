# 中小学生教学 Agent 平台需求分析文档（可直接用于 AI 生成代码）
**版本**: v1.0  
**文档类型**: PRD + 技术规范（面向 Java Web 实现）  
**目标**: 让 AI 编码助手可按本规范直接落地 MVP 与 V1

---

## 1. 项目目标与范围

### 1.1 项目目标
构建一个面向中小学生的 Web 教学 Agent，提供以下核心能力：

1. 作业/试卷图像上传与 OCR 文本提取  
2. 结合知识库进行智能批改（客观题优先，主观题渐进）  
3. 成绩统计与分布图展示  
4. 自动生成错题本并关联知识点  
5. 基于错题生成同类练习与复习建议

### 1.2 MVP 范围（必须）
- 角色：学生、老师、家长
- 学科：先支持 **数学（小学/初中）**
- 题型：选择题、填空题、判断题、简答题（简答先做要点匹配）
- 数据输入：图片上传（JPG/PNG）+ PDF
- 输出：批改结果、错题本、个人成绩趋势、班级分布（老师）

### 1.3 非目标（MVP 不做）
- 实时视频讲题
- 口语评分
- 自动组卷高级策略
- 全学科一次性覆盖

---

## 2. 名词定义（统一术语）

- **Submission**: 一次学生提交（可包含多页图像）
- **Question**: 结构化后的题目实体
- **StudentAnswer**: 学生作答内容
- **GradingResult**: 该题批改结果（得分、错因、置信度）
- **KnowledgePoint**: 知识点节点（章节-考点树）
- **WrongBookItem**: 错题本条目
- **LowConfidence**: 低置信度结果，需要人工复核
- **Provider**: 可插拔能力实现（OCR/LLM/RAG）

---

## 3. 用户角色与权限

### 3.1 角色
- `STUDENT`：上传作业、看批改、错题本、再练
- `TEACHER`：查看班级统计、复核低置信度题、导出报告
- `PARENT`：查看孩子学习报告、错题与建议
- `ADMIN`：租户配置、Provider 配置、系统参数管理

### 3.2 权限规则（必须）
- 学生仅能访问本人数据
- 家长仅能访问绑定学生数据
- 老师仅能访问所属班级学生数据
- 管理员按租户隔离访问（`tenantId` 强制透传）

---

## 4. 业务流程（端到端）

1. 学生上传作业（`Submission`）  
2. 系统创建异步任务（状态 `PENDING`）  
3. 图像预处理（去噪/旋转/切页）  
4. OCR 识别 + 题目结构化  
5. 批改引擎判分（规则优先，LLM 补充）  
6. 低置信度题目进入复核队列  
7. 生成错题本条目并映射知识点  
8. 更新成绩统计与图表聚合  
9. 学生/老师/家长查看结果

---

## 5. 功能需求（带验收标准）

---

### FR-01 上传与提交管理（必须）

#### 描述
支持单次提交多页作业图像或 PDF。

#### 输入
- 文件类型：`jpg/png/pdf`
- 单文件大小：<= 20MB
- 单次页数：<= 20 页

#### 输出
- `submissionId`
- 初始状态：`PENDING`

#### 验收标准
- 上传成功返回 201
- 不合规文件返回 400（带错误码）
- 文件元数据写库，原文件入对象存储

---

### FR-02 OCR 与题目结构化（必须）

#### 描述
将图像内容识别为结构化题目与作答。

#### 结构化结果（必须字段）
- `questionNo`
- `questionType`
- `stemText`
- `studentAnswerRaw`
- `bbox`（可选但建议）
- `ocrConfidence`（0-1）

#### 验收标准
- 能识别出题号与题干（数学场景优先）
- 识别失败页标记 `FAILED` 并可重试
- 低于阈值（默认 0.65）打 `LOW_CONFIDENCE`

---

### FR-03 智能批改（必须）

#### 描述
基于知识库与规则引擎进行判分，必要时调用 LLM。

#### 判分策略（必须）
1. 客观题：标准答案精准匹配（含格式归一）
2. 填空题：支持多答案、单位校验
3. 简答题：要点匹配 + LLM 辅助解释
4. 输出置信度，低置信度进入复核

#### 输出字段（必须）
- `isCorrect`
- `scoreObtained`
- `scoreFull`
- `errorTags`（数组）
- `knowledgePointIds`（数组）
- `explanation`
- `gradingConfidence`

#### 验收标准
- 客观题准确率 >= 98%（测试集）
- 每题必须返回可解释文本（`explanation`）
- 低置信度题可在老师端复核并回写最终结果

---

### FR-04 成绩分析与图表（必须）

#### 描述
展示个人与班级维度分析结果。

#### 指标
- 单次总分、分题得分
- 历次趋势（最近 N 次）
- 分布图（分段人数）
- 知识点正确率 Top/Bottom

#### 验收标准
- 查询接口 P95 < 500ms（缓存命中）
- 图表数据与批改明细一致

---

### FR-05 错题本（必须）

#### 描述
自动沉淀错题，支持筛选与重练。

#### 规则
- 错题自动入库
- 同题不重复入库（按题目指纹 + 学生）
- 支持按知识点/时间/掌握状态筛选

#### 验收标准
- 错题生成成功率 >= 99%
- 可一键生成 3 道同类题（基础/提升/挑战）

---

### FR-06 复核工作台（V1 必须）

#### 描述
老师处理低置信度与争议题。

#### 验收标准
- 支持查看 OCR 原图、识别文本、系统给分依据
- 复核后触发成绩重算与错题本更新

---

### FR-07 学习建议（V1）

#### 描述
基于错题与知识点薄弱项生成建议。

#### 验收标准
- 输出每周计划（可选 15/30/45 分钟）
- 建议必须可执行（含题量与目标知识点）

---

## 6. 非功能需求（NFR）

### NFR-01 性能
- 上传接口：P95 < 800ms（不含异步处理）
- OCR+批改总链路：单次提交 20 页内，90% 在 120 秒内完成
- 图表查询：P95 < 500ms

### NFR-02 可用性
- 服务可用性 >= 99.9%
- 异步任务支持失败重试（指数退避，最多 3 次）

### NFR-03 安全与合规
- 全链路 HTTPS
- PII 字段加密存储（手机号、姓名可逆加密）
- 日志脱敏（禁止输出完整姓名/手机号/图片 URL 签名）
- 支持数据删除请求（按学生维度）

### NFR-04 可观测性
- 必须上报：任务耗时、失败率、低置信度率、人工复核率、单次成本
- 每次批改记录 `traceId`、`providerVersion`、`promptVersion`

---

## 7. 技术架构约束（Java 体系）

## 7.1 技术栈（固定）
- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- PostgreSQL 15+
- Redis 7+
- RabbitMQ（或 Kafka，二选一）
- MinIO（或 S3 兼容）
- pgvector（知识检索）

### 7.2 架构原则（必须）
- `Provider` 可插拔（OCR/LLM/RAG）
- 业务接口与异步任务解耦
- 所有写操作幂等（`idempotencyKey`）
- 事件驱动 + Outbox Pattern（防止数据与消息不一致）

---

## 8. 数据模型（核心表）

> 以下为逻辑模型，AI 编码时需生成 Flyway/Liquibase 脚本。

### 8.1 用户与组织
- `tenant`
- `user`
- `student_profile`
- `parent_student_rel`
- `classroom`
- `class_enrollment`

### 8.2 作业与批改
- `assignment`
- `submission`
- `submission_file`
- `question`
- `student_answer`
- `grading_result`
- `grading_review`

### 8.3 知识库与错题
- `knowledge_point`
- `question_knowledge_rel`
- `wrong_book_item`
- `practice_task`

### 8.4 统计与任务
- `score_snapshot`
- `analytics_daily_agg`
- `job_task`
- `outbox_event`

---

## 9. 状态机规范（必须实现）

### 9.1 Submission 状态
`PENDING -> PROCESSING -> COMPLETED | FAILED | NEED_REVIEW`

### 9.2 JobTask 状态
`PENDING -> RUNNING -> SUCCESS | FAILED | RETRYING`

### 9.3 转移规则
- `FAILED` 允许人工触发 `RETRY`
- `NEED_REVIEW` 完成复核后必须进入 `COMPLETED`
- 非法状态跳转返回 409

---

## 10. API 规范（OpenAPI 风格）

> 前缀：`/api/v1`

### 10.1 创建提交
`POST /submissions`

**Request**
```json
{
  "assignmentId": "a_123",
  "studentId": "s_123",
  "files": [
    {"fileKey": "minio://bucket/xxx1.jpg"},
    {"fileKey": "minio://bucket/xxx2.jpg"}
  ]
}
```

**Response 201**
```json
{
  "submissionId": "sub_001",
  "status": "PENDING"
}
```

---

### 10.2 启动处理
`POST /submissions/{submissionId}/process`

**Response 202**
```json
{
  "submissionId": "sub_001",
  "jobId": "job_888",
  "status": "PROCESSING"
}
```

---

### 10.3 查询处理状态
`GET /submissions/{submissionId}/status`

**Response 200**
```json
{
  "submissionId": "sub_001",
  "status": "NEED_REVIEW",
  "progress": 80,
  "lowConfidenceCount": 2
}
```

---

### 10.4 查询批改结果
`GET /submissions/{submissionId}/results`

**Response 200**
```json
{
  "submissionId": "sub_001",
  "totalScore": 86,
  "fullScore": 100,
  "questions": [
    {
      "questionId": "q_1",
      "isCorrect": false,
      "scoreObtained": 0,
      "scoreFull": 5,
      "errorTags": ["CALCULATION_ERROR"],
      "knowledgePointIds": ["kp_fraction_add"],
      "explanation": "通分后分子相加时出现错误",
      "gradingConfidence": 0.78
    }
  ]
}
```

---

### 10.5 复核提交
`POST /reviews/{gradingResultId}/confirm`

**Request**
```json
{
  "finalScore": 3,
  "finalIsCorrect": false,
  "reviewComment": "步骤有部分正确，给3分"
}
```

**Response 200**
```json
{
  "gradingResultId": "gr_123",
  "reviewed": true
}
```

---

### 10.6 错题本查询
`GET /students/{studentId}/wrong-book?knowledgePointId=...&status=...`

### 10.7 生成同类练习
`POST /wrong-book/{itemId}/practice`

---

## 11. 批改与 AI 规则（关键约束）

### 11.1 Prompt 约束（必须）
- 必须要求模型输出 JSON（固定 schema）
- 禁止自由文本混杂（解析失败重试一次）
- 必须返回 `confidence`、`reasoning_summary`（简短）、`evidence_refs`

### 11.2 评分安全网（必须）
- 规则引擎优先于 LLM
- LLM 分数超过阈值偏差时触发二次校验
- 低置信度强制 `NEED_REVIEW`

### 11.3 内容安全（必须）
- 不直接提供整题答案（学生模式）
- 默认先给提示再给步骤（可配置教师模式）

---

## 12. 错误码规范

- `40001` 参数错误
- `40002` 文件类型不支持
- `40101` 未授权
- `40301` 无权限
- `40401` 资源不存在
- `40901` 状态冲突
- `42901` 请求限流
- `50001` 系统错误
- `50201` OCR Provider 异常
- `50202` LLM Provider 异常

---

## 13. 测试验收标准（可直接生成测试代码）

### 13.1 单元测试（必须）
- 覆盖率：核心域服务 >= 80%
- 包含：判分规则、状态机、权限校验、幂等逻辑

### 13.2 集成测试（必须）
- 上传到批改全链路
- OCR 失败重试
- 低置信度复核回写
- 错题本去重逻辑

### 13.3 E2E 测试（必须）
- 学生上传 -> 查看结果 -> 查看错题 -> 生成练习
- 老师复核 -> 成绩重算 -> 班级图表更新

### 13.4 性能测试（必须）
- 100 并发上传
- 任务堆积场景（队列长度 1w）稳定性
- 报表查询热点缓存命中率 > 90%

---

## 14. 代码生成执行规范（给 AI 编码器）

以下要求用于约束 AI 自动写代码：

1. 先生成项目骨架（多模块 Maven）
2. 再生成数据库迁移脚本（Flyway）
3. 再实现 Domain + Repository + Service + Controller
4. 所有接口先写 OpenAPI 注解
5. 异步任务采用统一 `JobTaskExecutor` 抽象
6. Provider 按接口 + 默认实现方式落地
7. 先写测试再补实现（至少关键服务 TDD）
8. 所有 DTO 字段必须有校验注解
9. 所有响应统一包装 `ApiResponse<T>`
10. 所有异常走全局异常处理器 `GlobalExceptionHandler`

---

## 15. 里程碑与交付物

### M1（第 1-2 周）
- 用户/组织/权限
- 上传与 Submission 主流程
- 基础任务队列框架

### M2（第 3-4 周）
- OCR 接入
- 题目结构化
- 客观题批改

### M3（第 5-6 周）
- 错题本
- 成绩统计与图表 API
- 基础复核流程

### M4（第 7-8 周）
- 简答题要点匹配
- 学习建议
- 监控与成本看板

---

## 16. 完成定义（Definition of Done）

功能被视为完成，必须同时满足：

- 需求项对应 API 已实现并通过测试
- OpenAPI 文档可访问且与实现一致
- 关键链路日志与 traceId 可追踪
- 监控面板可看到成功率/耗时/错误率
- 安全检查通过（鉴权、脱敏、权限）
- 已提供部署脚本（Docker Compose 或 K8s）

---

## 17. 后续扩展位（预留字段/能力）

- `subject`、`grade`、`edition`（教材版本）
- `providerConfig`（按租户配置 OCR/LLM）
- `modelVersion`、`promptVersion`
- `reviewPolicy`（低置信度阈值动态化）
- `pricingTag`（成本核算）
