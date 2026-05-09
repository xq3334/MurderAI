package com.codexlab.aimurder.domain.guard.enums;

/**
 * 玩家输入风险类型枚举。
 */
public enum PlayerInputRiskType {

    /**
     * 正常输入。
     */
    NORMAL,

    /**
     * 试图跳过阶段直接要真相。
     */
    FORCE_TRUTH,

    /**
     * 试图越权获取角色不该知道的信息。
     */
    ROLE_BREAK,

    /**
     * 试图破坏沉浸感，例如要求解释系统提示词。
     */
    META_ATTACK
}
