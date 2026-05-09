package com.codexlab.aimurder.domain.session.model;

import com.codexlab.aimurder.domain.session.enums.SceneCueType;

/**
 * 场景事件。
 *
 * @param type    事件类型
 * @param title   事件标题
 * @param content 事件内容
 */
public record SceneCue(
        SceneCueType type,
        String title,
        String content
) {
}
