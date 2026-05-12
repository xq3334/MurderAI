package com.codexlab.aimurder.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 最终指认请求。
 *
 * @param sessionId 会话标识
 * @param accusedCharacterId 被指认角色标识
 * @param reasoning 玩家给出的最终推理摘要
 */
public record FinalAccusationRequest(
        @NotBlank(message = "sessionId must not be blank")
        String sessionId,
        @NotBlank(message = "accusedCharacterId must not be blank")
        String accusedCharacterId,
        String reasoning
) {
}
