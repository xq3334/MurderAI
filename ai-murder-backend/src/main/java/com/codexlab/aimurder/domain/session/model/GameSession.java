package com.codexlab.aimurder.domain.session.model;

import com.codexlab.aimurder.domain.session.enums.GameSessionStatus;
import com.codexlab.aimurder.web.dto.ChatContextMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏会话。
 * 表示玩家实际开启的一局游戏的运行时状态。
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
