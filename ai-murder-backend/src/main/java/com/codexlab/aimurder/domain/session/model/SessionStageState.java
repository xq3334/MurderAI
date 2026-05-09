package com.codexlab.aimurder.domain.session.model;

import java.time.LocalDateTime;

/**
 * 当前阶段运行时状态。
 * 用于描述这一局目前处在哪一幕，以及何时进入该幕。
 */
public class SessionStageState {

    /**
     * 当前阶段标识。
     */
    private String stageId;

    /**
     * 当前阶段顺序。
     */
    private int stageOrder;

    /**
     * 当前阶段名称。
     */
    private String stageName;

    /**
     * 进入当前阶段的时间。
     */
    private LocalDateTime enteredAt;

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public int getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(int stageOrder) {
        this.stageOrder = stageOrder;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public LocalDateTime getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(LocalDateTime enteredAt) {
        this.enteredAt = enteredAt;
    }
}
