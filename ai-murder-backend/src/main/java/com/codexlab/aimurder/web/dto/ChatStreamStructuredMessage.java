package com.codexlab.aimurder.web.dto;

/**
 * SSE 结构化消息体。
 *
 * @param messageId  当前消息唯一标识
 * @param speaker    发言者显示名称
 * @param speakerKey 发言者稳定标识
 * @param role       发言角色类型
 * @param tone       发言语气标签
 * @param delta      本次新增文本片段
 * @param completed  当前消息是否已完成
 */
public record ChatStreamStructuredMessage(
        String messageId,
        String speaker,
        String speakerKey,
        StructuredMessageRole role,
        StructuredMessageKind kind,
        String tone,
        String delta,
        boolean completed
) {
}
