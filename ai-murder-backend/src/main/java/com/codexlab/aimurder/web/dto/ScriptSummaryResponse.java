package com.codexlab.aimurder.web.dto;

/**
 * 副本列表摘要。
 *
 * @param scriptId 副本标识
 * @param scriptName 副本名称
 * @param summary 副本简介
 * @param openingNarration 开场氛围文案
 * @param playerModeName 玩法模式名称
 * @param selectableRoleCount 可扮演角色数量
 */
public record ScriptSummaryResponse(
        String scriptId,
        String scriptName,
        String summary,
        String openingNarration,
        String playerModeName,
        int selectableRoleCount,
        int unlockOrder,
        boolean randomRoleOnStart
) {
}
