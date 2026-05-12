package com.codexlab.aimurder.web.dto;

import java.util.List;

/**
 * AI 提示响应。
 *
 * @param sessionId 当前会话标识
 * @param hints     面向玩家的三条推进建议
 */
public record ChatHintResponse(
        String sessionId,
        List<String> hints
) {
}
