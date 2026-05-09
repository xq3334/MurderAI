package com.codexlab.aimurder.web.dto;

import java.util.List;

/**
 * SSE 进度快照。
 *
 * @param scriptName      副本名称
 * @param playerRoleName  玩家身份
 * @param currentStageName 当前阶段名称
 * @param currentStageOrder 当前阶段顺序
 * @param totalStages     总阶段数
 * @param objective       当前目标
 * @param revealedClues   已公开线索
 */
public record ChatStreamProgressResponse(
        String scriptName,
        String playerRoleName,
        String currentStageName,
        int currentStageOrder,
        int totalStages,
        String objective,
        List<ClueProgressItem> revealedClues
) {
}
