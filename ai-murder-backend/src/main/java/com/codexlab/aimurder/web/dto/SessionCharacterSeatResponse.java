package com.codexlab.aimurder.web.dto;

/**
 * 会话中的角色席位摘要。
 *
 * @param characterId 角色标识
 * @param characterName 角色名称
 * @param identity 角色身份
 * @param mood 角色当前给人的情绪印象
 * @param status 角色当前值得关注的状态描述
 */
public record SessionCharacterSeatResponse(
        String characterId,
        String characterName,
        String identity,
        String mood,
        String status
) {
}
