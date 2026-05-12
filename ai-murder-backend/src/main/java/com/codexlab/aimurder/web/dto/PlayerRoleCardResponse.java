package com.codexlab.aimurder.web.dto;

/**
 * 玩家选角预览响应。
 * 这里只返回选角前允许公开的信息，避免在副本开始前泄露私密身份。
 *
 * @param characterId 角色标识
 * @param characterName 角色名
 * @param identity 角色身份
 * @param relationship 与死者或核心事件关系
 * @param publicPersona 对外人设
 * @param publicBackstory 公开背景
 * @param publicObjective 公开目标
 */
public record PlayerRoleCardResponse(
        String characterId,
        String characterName,
        String identity,
        String relationship,
        String publicPersona,
        String publicBackstory,
        String publicObjective
) {
}
