package com.codexlab.aimurder.domain.script.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * 副本总定义。
 * 用于描述一个副本的静态模板信息，不承载某一局游戏的运行时状态。
 */
public class ScriptDefinition {

    /**
     * 副本唯一标识。
     */
    private String scriptId;

    /**
     * 副本名称。
     */
    private String scriptName;

    /**
     * 副本简介。
     */
    private String summary;

    /**
     * 副本开场环境描述。
     */
    private String openingNarration;

    /**
     * 玩家在本副本中的身份名称。
     */
    private String playerRoleName;

    /**
     * 玩家身份说明。
     */
    private String playerRoleDescription;

    /**
     * 玩家本局目标。
     */
    private String playerObjective;

    /**
     * 开场引导要求。
     */
    private String openingInstruction;

    /**
     * 旁白使用规则。
     */
    private String narrationInstruction;

    /**
     * 角色定义列表。
     */
    private List<CharacterDefinition> characters = new ArrayList<>();

    /**
     * 阶段定义列表。
     */
    private List<StageDefinition> stages = new ArrayList<>();

    /**
     * 线索定义列表。
     */
    private List<ClueDefinition> clues = new ArrayList<>();

    /**
     * 最终真相描述。
     */
    private String truthSummary;

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOpeningNarration() {
        return openingNarration;
    }

    public void setOpeningNarration(String openingNarration) {
        this.openingNarration = openingNarration;
    }

    public String getPlayerRoleName() {
        return playerRoleName;
    }

    public void setPlayerRoleName(String playerRoleName) {
        this.playerRoleName = playerRoleName;
    }

    public String getPlayerRoleDescription() {
        return playerRoleDescription;
    }

    public void setPlayerRoleDescription(String playerRoleDescription) {
        this.playerRoleDescription = playerRoleDescription;
    }

    public String getPlayerObjective() {
        return playerObjective;
    }

    public void setPlayerObjective(String playerObjective) {
        this.playerObjective = playerObjective;
    }

    public String getOpeningInstruction() {
        return openingInstruction;
    }

    public void setOpeningInstruction(String openingInstruction) {
        this.openingInstruction = openingInstruction;
    }

    public String getNarrationInstruction() {
        return narrationInstruction;
    }

    public void setNarrationInstruction(String narrationInstruction) {
        this.narrationInstruction = narrationInstruction;
    }

    public List<CharacterDefinition> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterDefinition> characters) {
        this.characters = characters;
    }

    public List<StageDefinition> getStages() {
        return stages;
    }

    public void setStages(List<StageDefinition> stages) {
        this.stages = stages;
    }

    public List<ClueDefinition> getClues() {
        return clues;
    }

    public void setClues(List<ClueDefinition> clues) {
        this.clues = clues;
    }

    public String getTruthSummary() {
        return truthSummary;
    }

    public void setTruthSummary(String truthSummary) {
        this.truthSummary = truthSummary;
    }
}
