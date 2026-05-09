package com.codexlab.aimurder.domain.guard.model;

import com.codexlab.aimurder.domain.guard.enums.PlayerInputRiskType;

/**
 * 玩家输入守卫结果。
 * 用于在编排层先判定这一轮输入是否需要控场或改写回复策略。
 */
public class PlayerInputGuardResult {

    /**
     * 输入风险类型。
     */
    private PlayerInputRiskType riskType;

    /**
     * 是否允许直接进入正常回复流程。
     */
    private boolean allowed;

    /**
     * 推荐采用的兜底回复策略。
     */
    private String fallbackStrategy;

    /**
     * 守卫层给编排器的备注。
     */
    private String remark;

    public PlayerInputRiskType getRiskType() {
        return riskType;
    }

    public void setRiskType(PlayerInputRiskType riskType) {
        this.riskType = riskType;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getFallbackStrategy() {
        return fallbackStrategy;
    }

    public void setFallbackStrategy(String fallbackStrategy) {
        this.fallbackStrategy = fallbackStrategy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
