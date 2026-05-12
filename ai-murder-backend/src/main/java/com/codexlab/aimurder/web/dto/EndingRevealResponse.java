package com.codexlab.aimurder.web.dto;

import java.util.List;

/**
 * 结局揭晓响应。
 *
 * @param sessionId 会话标识
 * @param scriptId 副本标识
 * @param scriptName 副本名称
 * @param endingTitle 结局标题
 * @param success 指认是否正确
 * @param accusationAllowed 当前是否满足提交结案条件
 * @param verdict 结案判词
 * @param playerOutcome 玩家结果说明
 * @param accusedCharacterName 被指认角色名
 * @param killerCharacterName 真凶角色名
 * @param reasoningSummary 玩家推理摘要
 * @param truthStory 真相故事
 * @param keyEvidence 关键证据回放
 */
public record EndingRevealResponse(
        String sessionId,
        String scriptId,
        String scriptName,
        String endingTitle,
        boolean success,
        boolean accusationAllowed,
        String verdict,
        String playerOutcome,
        String accusedCharacterName,
        String killerCharacterName,
        String reasoningSummary,
        String truthStory,
        List<String> keyEvidence
) {
}
