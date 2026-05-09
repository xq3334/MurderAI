package com.codexlab.aimurder.web.service;

import com.codexlab.aimurder.domain.script.definition.CharacterDefinition;
import com.codexlab.aimurder.domain.script.definition.ClueDefinition;
import com.codexlab.aimurder.domain.script.definition.ScriptDefinition;
import com.codexlab.aimurder.domain.script.definition.StageDefinition;
import com.codexlab.aimurder.domain.session.enums.GameSessionStatus;
import com.codexlab.aimurder.domain.session.model.CharacterSessionState;
import com.codexlab.aimurder.domain.session.model.ClueRevealState;
import com.codexlab.aimurder.domain.session.model.GameSession;
import com.codexlab.aimurder.domain.session.model.SessionStageState;
import com.codexlab.aimurder.web.dto.ChatContextMessage;
import com.codexlab.aimurder.web.dto.ChatStreamProgressResponse;
import com.codexlab.aimurder.web.dto.ClueProgressItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏会话服务。
 * 负责维护某一局游戏的运行时状态。
 */
@Service
public class GameSessionService {

    private final Map<String, GameSession> sessionStore = new ConcurrentHashMap<>();
    private final ScriptRepository scriptRepository;

    public GameSessionService(ScriptRepository scriptRepository) {
        this.scriptRepository = scriptRepository;
    }

    /**
     * 获取会话，不存在则按默认副本创建。
     *
     * @param sessionId 会话标识
     * @return 游戏会话
     */
    public GameSession getOrCreate(String sessionId) {
        return sessionStore.computeIfAbsent(sessionId, key -> createSession(key, scriptRepository.getDefaultScript()));
    }

    /**
     * 追加一条消息到会话历史。
     *
     * @param sessionId 会话标识
     * @param role      消息角色
     * @param content   消息内容
     */
    public void appendMessage(String sessionId, String role, String content) {
        GameSession session = getOrCreate(sessionId);
        session.getMessageHistory().add(new ChatContextMessage(role, content));
        session.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 获取历史消息副本。
     *
     * @param sessionId 会话标识
     * @return 历史消息列表
     */
    public List<ChatContextMessage> getMessageHistory(String sessionId) {
        return new ArrayList<>(getOrCreate(sessionId).getMessageHistory());
    }

    /**
     * 判断当前会话是否已经完成开场。
     *
     * @param sessionId 会话标识
     * @return 是否已完成开场
     */
    public boolean isOpeningDelivered(String sessionId) {
        return getOrCreate(sessionId).isOpeningDelivered();
    }

    /**
     * 标记当前会话已完成开场引导。
     *
     * @param sessionId 会话标识
     */
    public void markOpeningDelivered(String sessionId) {
        GameSession session = getOrCreate(sessionId);
        session.setOpeningDelivered(true);
        session.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 刷新会话运行状态。
     *
     * @param session   游戏会话
     * @param message   玩家输入
     * @param speakerId 当前被点名的角色标识
     */
    public void refreshSessionState(GameSession session, String message, String speakerId) {
        int beforeStageOrder = session.getCurrentStage() == null ? 0 : session.getCurrentStage().getStageOrder();
        updateCharacterState(session, message, speakerId);
        updateStageState(session, message);
        int afterStageOrder = session.getCurrentStage() == null ? 0 : session.getCurrentStage().getStageOrder();
        if (afterStageOrder > beforeStageOrder) {
            revealCluesForCurrentStage(session);
        }
        session.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 获取并清空本轮待投放线索。
     *
     * @param sessionId 会话标识
     * @return 待投放线索列表
     */
    public List<ClueDefinition> consumePendingClues(String sessionId) {
        GameSession session = getOrCreate(sessionId);
        if (session.getPendingClueIds().isEmpty()) {
            return List.of();
        }

        ScriptDefinition scriptDefinition = scriptRepository.findById(session.getScriptId());
        List<ClueDefinition> clues = scriptDefinition.getClues().stream()
                .filter(clue -> session.getPendingClueIds().contains(clue.getClueId()))
                .toList();
        session.getPendingClueIds().clear();
        session.setUpdatedAt(LocalDateTime.now());
        return clues;
    }

    /**
     * 构建当前会话的进度快照。
     *
     * @param sessionId 会话标识
     * @return 进度快照
     */
    public ChatStreamProgressResponse buildProgress(String sessionId) {
        GameSession session = getOrCreate(sessionId);
        ScriptDefinition scriptDefinition = scriptRepository.findById(session.getScriptId());
        StageDefinition stageDefinition = getCurrentStageDefinition(session);
        List<ClueProgressItem> revealedClues = scriptDefinition.getClues().stream()
                .filter(clue -> session.getClueStates().stream()
                        .anyMatch(state -> state.getClueId().equals(clue.getClueId()) && state.isRevealed()))
                .map(clue -> new ClueProgressItem(
                        clue.getClueId(),
                        clue.getClueName(),
                        clue.getContent(),
                        clue.isKeyClue()
                ))
                .toList();

        return new ChatStreamProgressResponse(
                scriptDefinition.getScriptName(),
                scriptDefinition.getPlayerRoleName(),
                stageDefinition == null ? "" : stageDefinition.getStageName(),
                stageDefinition == null ? 0 : stageDefinition.getStageOrder(),
                scriptDefinition.getStages().size(),
                stageDefinition == null ? "" : stageDefinition.getObjective(),
                revealedClues
        );
    }

    /**
     * 获取当前阶段可公开的线索列表。
     *
     * @param session 游戏会话
     * @return 当前可公开线索
     */
    public List<ClueDefinition> getAvailableClues(GameSession session) {
        ScriptDefinition scriptDefinition = scriptRepository.findById(session.getScriptId());
        if (scriptDefinition == null || session.getCurrentStage() == null) {
            return List.of();
        }

        List<String> availableClueIds = scriptDefinition.getStages().stream()
                .filter(stage -> stage.getStageId().equals(session.getCurrentStage().getStageId()))
                .findFirst()
                .map(StageDefinition::getAvailableClueIds)
                .orElse(List.of());

        return scriptDefinition.getClues().stream()
                .filter(clue -> availableClueIds.contains(clue.getClueId()))
                .toList();
    }

    /**
     * 获取当前说话角色定义。
     *
     * @param session   游戏会话
     * @param speakerId 角色标识
     * @return 角色定义
     */
    public CharacterDefinition getSpeaker(GameSession session, String speakerId) {
        ScriptDefinition scriptDefinition = scriptRepository.findById(session.getScriptId());
        if (scriptDefinition == null) {
            return null;
        }

        return scriptDefinition.getCharacters().stream()
                .filter(character -> character.getCharacterId().equals(speakerId))
                .findFirst()
                .orElseGet(() -> scriptDefinition.getCharacters().stream()
                        .filter(character -> "butler".equals(character.getCharacterId()))
                        .findFirst()
                        .orElse(null));
    }

    /**
     * 获取当前阶段定义。
     *
     * @param session 游戏会话
     * @return 当前阶段定义
     */
    public StageDefinition getCurrentStageDefinition(GameSession session) {
        ScriptDefinition scriptDefinition = scriptRepository.findById(session.getScriptId());
        if (scriptDefinition == null || session.getCurrentStage() == null) {
            return null;
        }

        return scriptDefinition.getStages().stream()
                .filter(stage -> stage.getStageId().equals(session.getCurrentStage().getStageId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 创建新会话。
     *
     * @param sessionId        会话标识
     * @param scriptDefinition 副本定义
     * @return 新会话
     */
    private GameSession createSession(String sessionId, ScriptDefinition scriptDefinition) {
        GameSession session = new GameSession();
        session.setSessionId(sessionId);
        session.setScriptId(scriptDefinition.getScriptId());
        session.setStatus(GameSessionStatus.IN_PROGRESS);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        session.setCurrentStage(buildInitialStage(scriptDefinition));
        session.setCharacterStates(buildCharacterStates(scriptDefinition));
        session.setClueStates(buildClueStates(scriptDefinition));
        session.setPendingClueIds(new ArrayList<>());
        return session;
    }

    /**
     * 构建初始阶段状态。
     *
     * @param scriptDefinition 副本定义
     * @return 阶段状态
     */
    private SessionStageState buildInitialStage(ScriptDefinition scriptDefinition) {
        StageDefinition firstStage = scriptDefinition.getStages().stream()
                .min(Comparator.comparingInt(StageDefinition::getStageOrder))
                .orElseThrow();

        SessionStageState state = new SessionStageState();
        state.setStageId(firstStage.getStageId());
        state.setStageName(firstStage.getStageName());
        state.setStageOrder(firstStage.getStageOrder());
        state.setEnteredAt(LocalDateTime.now());
        return state;
    }

    /**
     * 构建角色运行时状态列表。
     *
     * @param scriptDefinition 副本定义
     * @return 角色状态列表
     */
    private List<CharacterSessionState> buildCharacterStates(ScriptDefinition scriptDefinition) {
        return scriptDefinition.getCharacters().stream()
                .filter(character -> !"butler".equals(character.getCharacterId()))
                .map(character -> {
                    CharacterSessionState state = new CharacterSessionState();
                    state.setCharacterId(character.getCharacterId());
                    state.setPressureLevel(0);
                    state.setSuspected(false);
                    state.setLoosened(false);
                    return state;
                })
                .toList();
    }

    /**
     * 构建线索公开状态列表。
     *
     * @param scriptDefinition 副本定义
     * @return 线索状态列表
     */
    private List<ClueRevealState> buildClueStates(ScriptDefinition scriptDefinition) {
        return scriptDefinition.getClues().stream()
                .map(clue -> {
                    ClueRevealState state = new ClueRevealState();
                    state.setClueId(clue.getClueId());
                    state.setRevealed(false);
                    return state;
                })
                .toList();
    }

    /**
     * 根据玩家输入更新角色压力和怀疑状态。
     *
     * @param session   游戏会话
     * @param message   玩家输入
     * @param speakerId 当前角色标识
     */
    private void updateCharacterState(GameSession session, String message, String speakerId) {
        String normalizedMessage = message == null ? "" : message;
        session.getCharacterStates().forEach(state -> {
            if (state.getCharacterId().equals(speakerId) && !"butler".equals(speakerId)) {
                state.setPressureLevel(state.getPressureLevel() + 1);
            }

            if (state.getCharacterId().equals(speakerId)
                    && containsAny(normalizedMessage, "怀疑", "是不是你", "凶手", "你杀")) {
                state.setSuspected(true);
            }

            if (state.getPressureLevel() >= 3) {
                state.setLoosened(true);
            }
        });
    }

    /**
     * 根据玩家输入做轻量阶段推进。
     *
     * @param session 游戏会话
     * @param message 玩家输入
     */
    private void updateStageState(GameSession session, String message) {
        String normalizedMessage = message == null ? "" : message;
        SessionStageState currentStage = session.getCurrentStage();
        if (currentStage == null) {
            return;
        }

        if (currentStage.getStageOrder() == 1
                && containsAny(normalizedMessage, "停电", "配电箱", "人为", "谁动了电")) {
            session.setCurrentStage(buildStageState(session.getScriptId(), 2));
            return;
        }

        if (currentStage.getStageOrder() == 2
                && containsAny(normalizedMessage, "账本", "残页", "字迹", "便签", "乔")) {
            session.setCurrentStage(buildStageState(session.getScriptId(), 3));
        }
    }

    /**
     * 根据阶段顺序构建阶段状态。
     *
     * @param scriptId   副本标识
     * @param stageOrder 阶段顺序
     * @return 阶段状态
     */
    private SessionStageState buildStageState(String scriptId, int stageOrder) {
        ScriptDefinition scriptDefinition = scriptRepository.findById(scriptId);
        StageDefinition stageDefinition = scriptDefinition.getStages().stream()
                .filter(stage -> stage.getStageOrder() == stageOrder)
                .findFirst()
                .orElseThrow();

        SessionStageState state = new SessionStageState();
        state.setStageId(stageDefinition.getStageId());
        state.setStageName(stageDefinition.getStageName());
        state.setStageOrder(stageDefinition.getStageOrder());
        state.setEnteredAt(LocalDateTime.now());
        return state;
    }

    /**
     * 为当前阶段公开线索，并记录为待投放线索。
     *
     * @param session 游戏会话
     */
    private void revealCluesForCurrentStage(GameSession session) {
        ScriptDefinition scriptDefinition = scriptRepository.findById(session.getScriptId());
        StageDefinition currentStage = getCurrentStageDefinition(session);
        if (scriptDefinition == null || currentStage == null) {
            return;
        }

        for (String clueId : currentStage.getAvailableClueIds()) {
            ClueRevealState state = session.getClueStates().stream()
                    .filter(item -> item.getClueId().equals(clueId))
                    .findFirst()
                    .orElse(null);
            if (state == null || state.isRevealed()) {
                continue;
            }
            state.setRevealed(true);
            state.setRevealedAt(LocalDateTime.now());
            session.getPendingClueIds().add(clueId);
        }
    }

    /**
     * 判断文本中是否包含任意片段。
     *
     * @param source    待检测文本
     * @param fragments 目标片段列表
     * @return 是否命中
     */
    private boolean containsAny(String source, String... fragments) {
        for (String fragment : fragments) {
            if (source.contains(fragment)) {
                return true;
            }
        }
        return false;
    }
}
