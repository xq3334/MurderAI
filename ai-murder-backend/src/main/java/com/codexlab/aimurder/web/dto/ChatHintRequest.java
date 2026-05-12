package com.codexlab.aimurder.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AI 提示请求。
 *
 * @param sessionId 当前会话标识
 */
public record ChatHintRequest(
        @NotBlank(message = "sessionId must not be blank")
        String sessionId
) {
}
