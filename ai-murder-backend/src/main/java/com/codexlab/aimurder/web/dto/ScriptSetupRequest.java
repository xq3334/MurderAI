package com.codexlab.aimurder.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 初始化副本会话请求。
 *
 * @param sessionId 会话标识，可为空
 * @param scriptId 副本标识
 * @param playerCharacterId 玩家角色标识
 */
public record ScriptSetupRequest(
        String sessionId,
        @NotBlank(message = "scriptId must not be blank")
        String scriptId,
        @NotBlank(message = "playerCharacterId must not be blank")
        String playerCharacterId
) {
}
