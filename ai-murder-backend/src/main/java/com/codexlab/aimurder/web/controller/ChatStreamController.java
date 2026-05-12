package com.codexlab.aimurder.web.controller;

import com.codexlab.aimurder.web.dto.ChatHintRequest;
import com.codexlab.aimurder.web.dto.ChatHintResponse;
import com.codexlab.aimurder.web.dto.ChatStreamRequest;
import com.codexlab.aimurder.web.dto.Result;
import com.codexlab.aimurder.web.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/chat")
public class ChatStreamController {

    private final ChatService chatService;

    public ChatStreamController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 打开一次流式对话通道，并通过 SSE 持续返回当前请求的响应事件。
     *
     * @param request 对话请求，包含会话标识和用户输入
     * @return 用于增量推送响应内容的 SSE 发射器
     */
    @PostMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@Valid @RequestBody ChatStreamRequest request) {
        return chatService.streamChat(request.sessionId(), request.message());
    }

    @PostMapping("/hints")
    public Result<ChatHintResponse> hints(@Valid @RequestBody ChatHintRequest request) {
        return Result.success(chatService.generateHints(request.sessionId()));
    }
}
