package com.codexlab.aimurder.web.dto;

/**
 * 流式结构化消息角色类型。
 */
public enum StructuredMessageRole {

    /**
     * 管家或系统控场发言。
     */
    SYSTEM,

    /**
     * 玩家发言。
     */
    PLAYER,

    /**
     * 剧中角色发言。
     */
    CHARACTER,

    /**
     * 旁白发言。
     */
    NARRATOR
}
