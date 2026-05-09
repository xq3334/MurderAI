package com.codexlab.aimurder.domain.script.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * 阶段定义。
 * 用于约束某一幕允许公开的内容、目标和推进条件。
 */
public class StageDefinition {

    /**
     * 阶段唯一标识。
     */
    private String stageId;

    /**
     * 阶段名称。
     */
    private String stageName;

    /**
     * 阶段顺序。
     */
    private int stageOrder;

    /**
     * 阶段目标说明。
     */
    private String objective;

    /**
     * 阶段开场文案。
     */
    private String openingNarration;

    /**
     * 阶段允许公开的线索标识集合。
     */
    private List<String> availableClueIds = new ArrayList<>();

    /**
     * 阶段允许重点追问的角色标识集合。
     */
    private List<String> focusCharacterIds = new ArrayList<>();

    /**
     * 进入下一阶段的条件描述。
     */
    private String nextStageCondition;

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public int getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(int stageOrder) {
        this.stageOrder = stageOrder;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getOpeningNarration() {
        return openingNarration;
    }

    public void setOpeningNarration(String openingNarration) {
        this.openingNarration = openingNarration;
    }

    public List<String> getAvailableClueIds() {
        return availableClueIds;
    }

    public void setAvailableClueIds(List<String> availableClueIds) {
        this.availableClueIds = availableClueIds;
    }

    public List<String> getFocusCharacterIds() {
        return focusCharacterIds;
    }

    public void setFocusCharacterIds(List<String> focusCharacterIds) {
        this.focusCharacterIds = focusCharacterIds;
    }

    public String getNextStageCondition() {
        return nextStageCondition;
    }

    public void setNextStageCondition(String nextStageCondition) {
        this.nextStageCondition = nextStageCondition;
    }
}
