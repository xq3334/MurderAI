package com.codexlab.aimurder.web.dto;

/**
 * 表示存储在内存上下文中的单条对话消息。
 *
 * @param role    消息角色，例如 user 或 assistant
 * @param content 消息正文内容
 */
public record ChatContextMessage(
        String role,
        String content
) {
}
