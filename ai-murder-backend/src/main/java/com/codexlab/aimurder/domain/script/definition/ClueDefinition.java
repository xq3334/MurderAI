package com.codexlab.aimurder.domain.script.definition;

import com.codexlab.aimurder.domain.script.enums.ClueType;

import java.util.ArrayList;
import java.util.List;

/**
 * 线索定义。
 * 用于描述线索内容、解锁条件和对推理链的影响。
 */
public class ClueDefinition {

    /**
     * 线索唯一标识。
     */
    private String clueId;

    /**
     * 线索名称。
     */
    private String clueName;

    /**
     * 线索类型。
     */
    private ClueType clueType;

    /**
     * 线索展示给玩家的内容。
     */
    private String content;

    /**
     * 线索作用说明。
     */
    private String effect;

    /**
     * 线索首次允许出现的阶段标识。
     */
    private String unlockStageId;

    /**
     * 关联角色标识。
     */
    private List<String> relatedCharacterIds = new ArrayList<>();

    /**
     * 是否为关键线索。
     */
    private boolean keyClue;

    public String getClueId() {
        return clueId;
    }

    public void setClueId(String clueId) {
        this.clueId = clueId;
    }

    public String getClueName() {
        return clueName;
    }

    public void setClueName(String clueName) {
        this.clueName = clueName;
    }

    public ClueType getClueType() {
        return clueType;
    }

    public void setClueType(ClueType clueType) {
        this.clueType = clueType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getUnlockStageId() {
        return unlockStageId;
    }

    public void setUnlockStageId(String unlockStageId) {
        this.unlockStageId = unlockStageId;
    }

    public List<String> getRelatedCharacterIds() {
        return relatedCharacterIds;
    }

    public void setRelatedCharacterIds(List<String> relatedCharacterIds) {
        this.relatedCharacterIds = relatedCharacterIds;
    }

    public boolean isKeyClue() {
        return keyClue;
    }

    public void setKeyClue(boolean keyClue) {
        this.keyClue = keyClue;
    }
}
