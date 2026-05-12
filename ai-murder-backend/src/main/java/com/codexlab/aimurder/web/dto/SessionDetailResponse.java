package com.codexlab.aimurder.web.dto;

import java.util.List;

/**
 * 会话详情响应。
 *
 * @param sessionId 会话标识
 * @param scriptId 副本标识
 * @param scriptName 副本名称
 * @param playerCharacterId 玩家角色标识
 * @param playerCharacterName 玩家角色名
 * @param playerIdentity 玩家身份
 * @param playerRoleDescription 玩家角色卡摘要
 * @param playerObjective 玩家私密目标
 * @param openingDelivered 是否已完成开场
 * @param characterSeats 当前局中的其他角色席位
 * @param progress 当前进度快照
 */
public record SessionDetailResponse(
        String sessionId,
        String scriptId,
        String scriptName,
        String playerCharacterId,
        String playerCharacterName,
        String playerIdentity,
        String playerRoleDescription,
        String playerObjective,
        boolean openingDelivered,
        List<SessionCharacterSeatResponse> characterSeats,
        ChatStreamProgressResponse progress
) {
}
