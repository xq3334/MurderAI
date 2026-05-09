package com.codexlab.aimurder.web.dto;

public record ChatStreamEventResponse(
        String event,
        String sessionId,
        String content,
        boolean completed,
        ChatStreamStructuredMessage message,
        ChatStreamProgressResponse progress
) {
}
