package com.codexlab.aimurder.web.dto;

/**
 * 会话初始化结果。
 *
 * @param sessionId 会话标识
 * @param scriptId 副本标识
 * @param scriptName 副本名称
 * @param playerCharacterId 玩家角色标识
 * @param playerCharacterName 玩家角色名
 * @param playerIdentity 玩家身份
 * @param playerRoleDescription 玩家角色卡摘要
 * @param playerObjective 玩家私密目标
 * @param openingNarration 开场环境文案
 */
public record SessionBootstrapResponse(
        String sessionId,
        String scriptId,
        String scriptName,
        String playerCharacterId,
        String playerCharacterName,
        String playerIdentity,
        String playerRoleDescription,
        String playerObjective,
        String openingNarration
) {
}
