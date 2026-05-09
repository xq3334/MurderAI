# ai-murder-backend

AI 剧本杀后端项目骨架。

## 技术栈

- JDK 17
- Spring Boot
- Spring AI Alibaba
- Maven

## 当前阶段

当前仅完成基础项目初始化：

- Web 基础能力
- Validation
- Actuator
- Spring AI Alibaba DashScope starter
- 基础启动类
- 基础配置文件
- 基础测试类
- SSE 对话接口骨架

## 环境变量

运行前请准备：

- `AI_DASHSCOPE_API_KEY`

## 当前接口

### SSE 对话接口

- `POST /api/chat/stream`
- `Content-Type: application/json`
- `Accept: text/event-stream`

请求示例：

```json
{
  "sessionId": "session-demo",
  "message": "我想先试试对话流"
}
```

当前会返回三类事件：

- `start`
- `chunk`
- `complete`

注意：

- SSE 响应本身是事件流
- 每条事件的 `data` 统一为 `Result<T>` 结构

## 后续建议

下一步再继续拆：

1. `config`
2. `web`
3. `application`
4. `domain`
5. `infrastructure`
