package com.codexlab.aimurder.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 发起一次流式对话请求时使用的请求体。
 *
 * @param sessionId 可选会话标识，用于续接历史对话
 * @param message   当前这轮用户输入内容
 */
public record ChatStreamRequest(
        String sessionId,
        @NotBlank(message = "message must not be blank")
        String message
) {
}
