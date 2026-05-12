package com.codexlab.aimurder.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 随机初始化副本会话请求。
 *
 * @param sessionId 会话标识，可为空
 * @param scriptId 副本标识
 */
public record RandomScriptSetupRequest(
        String sessionId,
        @NotBlank(message = "scriptId must not be blank")
        String scriptId
) {
}
