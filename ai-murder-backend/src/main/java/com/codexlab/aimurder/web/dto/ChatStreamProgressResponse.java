package com.codexlab.aimurder.web.dto;

import java.util.List;

/**
 * SSE 进度快照。
 *
 * @param scriptName 当前副本名称
 * @param playerRoleName 玩家身份
 * @param currentStageName 当前阶段名称
 * @param currentStageOrder 当前阶段顺序
 * @param totalStages 总阶段数
 * @param objective 当前目标
 * @param atmosphere 当前环境气氛
 * @param storyBeat 当前剧情推进摘要
 * @param playerTurnCount 玩家已发起回合数
 * @param revealedClues 已公开线索
 */
public record ChatStreamProgressResponse(
        String scriptName,
        String playerRoleName,
        String currentStageName,
        int currentStageOrder,
        int totalStages,
        String objective,
        String atmosphere,
        String storyBeat,
        int playerTurnCount,
        List<ClueProgressItem> revealedClues
) {
}
