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
     * 当前副本的玩法模式名称。
     */
    private String playerModeName;

    private int unlockOrder = 1;

    private boolean randomRoleOnStart;

    /**
     * 当前副本的玩法模式说明。
     */
    private String playerModeDescription;

    /**
     * 控场角色标识。
     */
    private String hostCharacterId;

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

    /**
     * 结局揭晓标题。
     */
    private String endingTitle;

    /**
     * 结局真相长文。
     */
    private String endingStory;

    /**
     * 完成最终指认前至少需要公开的关键线索数量。
     */
    private int minimumKeyCluesForAccusation = 2;

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

    public String getPlayerModeName() {
        return playerModeName;
    }

    public void setPlayerModeName(String playerModeName) {
        this.playerModeName = playerModeName;
    }

    public int getUnlockOrder() {
        return unlockOrder;
    }

    public void setUnlockOrder(int unlockOrder) {
        this.unlockOrder = unlockOrder;
    }

    public boolean isRandomRoleOnStart() {
        return randomRoleOnStart;
    }

    public void setRandomRoleOnStart(boolean randomRoleOnStart) {
        this.randomRoleOnStart = randomRoleOnStart;
    }

    public String getPlayerModeDescription() {
        return playerModeDescription;
    }

    public void setPlayerModeDescription(String playerModeDescription) {
        this.playerModeDescription = playerModeDescription;
    }

    public String getHostCharacterId() {
        return hostCharacterId;
    }

    public void setHostCharacterId(String hostCharacterId) {
        this.hostCharacterId = hostCharacterId;
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

    public String getEndingTitle() {
        return endingTitle;
    }

    public void setEndingTitle(String endingTitle) {
        this.endingTitle = endingTitle;
    }

    public String getEndingStory() {
        return endingStory;
    }

    public void setEndingStory(String endingStory) {
        this.endingStory = endingStory;
    }

    public int getMinimumKeyCluesForAccusation() {
        return minimumKeyCluesForAccusation;
    }

    public void setMinimumKeyCluesForAccusation(int minimumKeyCluesForAccusation) {
        this.minimumKeyCluesForAccusation = minimumKeyCluesForAccusation;
    }
}
