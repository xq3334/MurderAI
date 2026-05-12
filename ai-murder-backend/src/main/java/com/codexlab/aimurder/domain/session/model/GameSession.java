package com.codexlab.aimurder.domain.session.model;

import com.codexlab.aimurder.domain.session.enums.GameSessionStatus;
import com.codexlab.aimurder.web.dto.ChatContextMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏会话。
 * 表示玩家实际开启的一局游戏在运行时的完整状态。
 */
public class GameSession {

    /**
     * 会话唯一标识。
     */
    private String sessionId;

    /**
     * 当前会话绑定的副本标识。
     */
    private String scriptId;

    /**
     * 玩家当前扮演的角色标识。
     */
    private String playerCharacterId;

    /**
     * 玩家当前扮演的角色名称。
     */
    private String playerCharacterName;

    /**
     * 玩家当前扮演角色的身份。
     */
    private String playerIdentity;

    /**
     * 玩家角色卡中的公开与私密背景摘要。
     */
    private String playerRoleDescription;

    /**
     * 玩家当前角色的私密目标。
     */
    private String playerObjective;

    /**
     * 控场角色标识。
     */
    private String hostCharacterId;

    /**
     * 当前阶段状态。
     */
    private SessionStageState currentStage;

    /**
     * 已公开线索状态列表。
     */
    private List<ClueRevealState> clueStates = new ArrayList<>();

    /**
     * 角色运行时状态列表。
     */
    private List<CharacterSessionState> characterStates = new ArrayList<>();

    /**
     * 玩家与系统的消息历史。
     */
    private List<ChatContextMessage> messageHistory = new ArrayList<>();

    /**
     * 玩家当前的结论或最终指认。
     */
    private String playerConclusion;

    /**
     * 是否已经完成过正式开场引导。
     */
    private boolean openingDelivered;

    /**
     * 待投放的线索标识列表。
     */
    private List<String> pendingClueIds = new ArrayList<>();

    /**
     * 待投放的场景旁白列表。
     */
    private List<SceneCue> pendingSceneCues = new ArrayList<>();

    /**
     * 玩家已发起的回合数。
     */
    private int playerTurnCount;

    /**
     * 当前阶段内已推进的回合数。
     */
    private int stageTurnCount;

    /**
     * 当前环境摘要。
     */
    private String currentEnvironmentSummary;

    /**
     * 当前剧情节拍摘要。
     */
    private String currentStoryBeat;

    /**
     * 当前这一轮是否刚发生阶段推进。
     */
    private boolean stageJustChanged;

    /**
     * 当前会话状态。
     */
    private GameSessionStatus status;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 最近更新时间。
     */
    private LocalDateTime updatedAt;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getPlayerCharacterId() {
        return playerCharacterId;
    }

    public void setPlayerCharacterId(String playerCharacterId) {
        this.playerCharacterId = playerCharacterId;
    }

    public String getPlayerCharacterName() {
        return playerCharacterName;
    }

    public void setPlayerCharacterName(String playerCharacterName) {
        this.playerCharacterName = playerCharacterName;
    }

    public String getPlayerIdentity() {
        return playerIdentity;
    }

    public void setPlayerIdentity(String playerIdentity) {
        this.playerIdentity = playerIdentity;
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

    public String getHostCharacterId() {
        return hostCharacterId;
    }

    public void setHostCharacterId(String hostCharacterId) {
        this.hostCharacterId = hostCharacterId;
    }

    public SessionStageState getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(SessionStageState currentStage) {
        this.currentStage = currentStage;
    }

    public List<ClueRevealState> getClueStates() {
        return clueStates;
    }

    public void setClueStates(List<ClueRevealState> clueStates) {
        this.clueStates = clueStates;
    }

    public List<CharacterSessionState> getCharacterStates() {
        return characterStates;
    }

    public void setCharacterStates(List<CharacterSessionState> characterStates) {
        this.characterStates = characterStates;
    }

    public List<ChatContextMessage> getMessageHistory() {
        return messageHistory;
    }

    public void setMessageHistory(List<ChatContextMessage> messageHistory) {
        this.messageHistory = messageHistory;
    }

    public String getPlayerConclusion() {
        return playerConclusion;
    }

    public void setPlayerConclusion(String playerConclusion) {
        this.playerConclusion = playerConclusion;
    }

    public boolean isOpeningDelivered() {
        return openingDelivered;
    }

    public void setOpeningDelivered(boolean openingDelivered) {
        this.openingDelivered = openingDelivered;
    }

    public List<String> getPendingClueIds() {
        return pendingClueIds;
    }

    public void setPendingClueIds(List<String> pendingClueIds) {
        this.pendingClueIds = pendingClueIds;
    }

    public List<SceneCue> getPendingSceneCues() {
        return pendingSceneCues;
    }

    public void setPendingSceneCues(List<SceneCue> pendingSceneCues) {
        this.pendingSceneCues = pendingSceneCues;
    }

    public int getPlayerTurnCount() {
        return playerTurnCount;
    }

    public void setPlayerTurnCount(int playerTurnCount) {
        this.playerTurnCount = playerTurnCount;
    }

    public int getStageTurnCount() {
        return stageTurnCount;
    }

    public void setStageTurnCount(int stageTurnCount) {
        this.stageTurnCount = stageTurnCount;
    }

    public String getCurrentEnvironmentSummary() {
        return currentEnvironmentSummary;
    }

    public void setCurrentEnvironmentSummary(String currentEnvironmentSummary) {
        this.currentEnvironmentSummary = currentEnvironmentSummary;
    }

    public String getCurrentStoryBeat() {
        return currentStoryBeat;
    }

    public void setCurrentStoryBeat(String currentStoryBeat) {
        this.currentStoryBeat = currentStoryBeat;
    }

    public boolean isStageJustChanged() {
        return stageJustChanged;
    }

    public void setStageJustChanged(boolean stageJustChanged) {
        this.stageJustChanged = stageJustChanged;
    }

    public GameSessionStatus getStatus() {
        return status;
    }

    public void setStatus(GameSessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
