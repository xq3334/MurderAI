package com.codexlab.aimurder.web.dto;

/**
 * 前端进度区使用的线索摘要。
 *
 * @param clueId   线索标识
 * @param clueName 线索名称
 * @param content  线索内容
 * @param keyClue  是否关键线索
 */
public record ClueProgressItem(
        String clueId,
        String clueName,
        String content,
        boolean keyClue
) {
}
