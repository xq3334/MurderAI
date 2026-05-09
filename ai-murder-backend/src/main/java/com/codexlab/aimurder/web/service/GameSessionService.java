package com.codexlab.aimurder.web.service;

import com.codexlab.aimurder.domain.script.definition.CharacterDefinition;
import com.codexlab.aimurder.domain.script.definition.ClueDefinition;
import com.codexlab.aimurder.domain.script.definition.ScriptDefinition;
import com.codexlab.aimurder.domain.script.definition.StageDefinition;
import com.codexlab.aimurder.domain.session.enums.GameSessionStatus;
import com.codexlab.aimurder.domain.session.enums.SceneCueType;
import com.codexlab.aimurder.domain.session.model.CharacterSessionState;
import com.codexlab.aimurder.domain.session.model.ClueRevealState;
import com.codexlab.aimurder.domain.session.model.GameSession;
import com.codexlab.aimurder.domain.session.model.SceneCue;
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
     * @return 是否已开场
     */
    public boolean isOpeningDelivered(String sessionId) {
        return getOrCreate(sessionId).isOpeningDelivered();
    }

    /**
     * 标记当前会话已经完成正式开场。
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
        session.setPlayerTurnCount(session.getPlayerTurnCount() + 1);
        session.setStageTurnCount(session.getStageTurnCount() + 1);

        updateCharacterState(session, message, speakerId);
        updateStageState(session, message);

        int afterStageOrder = session.getCurrentStage() == null ? 0 : session.getCurrentStage().getStageOrder();
        boolean stageChanged = afterStageOrder > beforeStageOrder;
        session.setStageJustChanged(stageChanged);

        if (stageChanged) {
            session.setStageTurnCount(0);
            revealCluesForCurrentStage(session);
        }

        updateSceneState(session, message);
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
     * 获取并清空本轮待投放的环境旁白。
     *
     * @param sessionId 会话标识
     * @return 待投放环境旁白列表
     */
    public List<SceneCue> consumePendingSceneCues(String sessionId) {
        GameSession session = getOrCreate(sessionId);
        if (session.getPendingSceneCues().isEmpty()) {
            return List.of();
        }

        List<SceneCue> sceneCues = new ArrayList<>(session.getPendingSceneCues());
        session.getPendingSceneCues().clear();
        session.setUpdatedAt(LocalDateTime.now());
        return sceneCues;
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
                session.getCurrentEnvironmentSummary(),
                session.getCurrentStoryBeat(),
                session.getPlayerTurnCount(),
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
     * 当前是否还处于入局演出期。
     *
     * @param session 游戏会话
     * @return 是否处于入局演出期
     */
    public boolean isInPrologue(GameSession session) {
        return session.getPlayerTurnCount() <= 1;
    }

    /**
     * 当前是否刚发生阶段切换。
     *
     * @param session 游戏会话
     * @return 是否刚切阶段
     */
    public boolean isStageJustChanged(GameSession session) {
        return session.isStageJustChanged();
    }

    /**
     * 获取当前环境摘要。
     *
     * @param session 游戏会话
     * @return 环境摘要
     */
    public String getEnvironmentSummary(GameSession session) {
        return session.getCurrentEnvironmentSummary();
    }

    /**
     * 获取当前剧情节拍摘要。
     *
     * @param session 游戏会话
     * @return 剧情节拍摘要
     */
    public String getStoryBeat(GameSession session) {
        return session.getCurrentStoryBeat();
    }

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
        session.setPendingSceneCues(new ArrayList<>());
        session.setPlayerTurnCount(0);
        session.setStageTurnCount(0);
        session.setStageJustChanged(false);
        session.setCurrentEnvironmentSummary(scriptDefinition.getOpeningNarration());
        session.setCurrentStoryBeat("众人刚被困在封闭现场，真正的互相试探尚未开始。");
        queueSceneCue(session, SceneCueType.ENTRY, "入局", "雨声裹着山庄外墙一路往下坠，昏黄的应急灯把每个人的影子都拉得比平时更长。");
        return session;
    }

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

    private void updateStageState(GameSession session, String message) {
        String normalizedMessage = message == null ? "" : message;
        SessionStageState currentStage = session.getCurrentStage();
        if (currentStage == null) {
            return;
        }

        boolean atmosphereReady = session.getPlayerTurnCount() >= 2;
        boolean pressureReady = session.getCharacterStates().stream()
                .anyMatch(state -> state.getPressureLevel() >= 2);

        if (currentStage.getStageOrder() == 1
                && atmosphereReady
                && (containsAny(normalizedMessage, "停电", "配电箱", "人为", "谁动了电")
                || pressureReady)) {
            session.setCurrentStage(buildStageState(session.getScriptId(), 2));
            return;
        }

        long loosenedCount = session.getCharacterStates().stream()
                .filter(CharacterSessionState::isLoosened)
                .count();
        boolean enoughPressure = loosenedCount >= 1 || session.getPlayerTurnCount() >= 4;
        if (currentStage.getStageOrder() == 2
                && enoughPressure
                && containsAny(normalizedMessage, "账本", "残页", "字迹", "便签", "遗嘱", "乔")) {
            session.setCurrentStage(buildStageState(session.getScriptId(), 3));
        }
    }

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

    private void updateSceneState(GameSession session, String message) {
        StageDefinition stage = getCurrentStageDefinition(session);
        if (stage == null) {
            return;
        }

        int turnCount = session.getPlayerTurnCount();
        String normalizedMessage = message == null ? "" : message;

        if (session.isStageJustChanged()) {
            session.setCurrentEnvironmentSummary(stage.getOpeningNarration());
            session.setCurrentStoryBeat("现场刚进入“" + stage.getStageName() + "”，众人的表情和口风都开始发生变化。");
            queueSceneCue(session, SceneCueType.TRANSITION, "转场", stage.getOpeningNarration());
            return;
        }

        if (turnCount == 1) {
            session.setCurrentEnvironmentSummary("雨声压在窗外，烛火和应急灯把每个人的脸都切出忽明忽暗的边。");
            session.setCurrentStoryBeat("这还不是正式盘问，更像是所有人被迫坐回现场、彼此观察第一眼的时候。");
            queueSceneCue(session, SceneCueType.ENTRY, "试探", "第一轮发问还没真正刺进去，桌边的人却已经开始用沉默互相衡量。");
            return;
        }

        if (stage.getStageOrder() == 1) {
            if (containsAny(normalizedMessage, "谁", "是不是", "为什么")) {
                session.setCurrentEnvironmentSummary("走廊尽头偶尔传来木板轻响，像有人稍微挪动了一下重心。");
                session.setCurrentStoryBeat("试探已经开始，表面平静的口供里第一次露出了互相防备的味道。");
                queueSceneCue(session, SceneCueType.PRESSURE, "压迫", "有人先开了口，空气反而更绷紧了，像再多追问一句就会把某根线扯断。");
            } else {
                session.setCurrentEnvironmentSummary("雨势没有减弱，山庄里却比刚停电时更安静，像所有人都在等别人先犯错。");
                session.setCurrentStoryBeat("众人仍在建立口供表面的一致性，但真正的紧张已经在桌面下蔓延。");
            }
            return;
        }

        if (stage.getStageOrder() == 2) {
            if (containsAny(normalizedMessage, "停电", "配电箱", "线路")) {
                session.setCurrentEnvironmentSummary("有人不自觉看向走廊外侧，像是那只配电箱忽然比尸体还更让人心虚。");
                session.setCurrentStoryBeat("调查重点已经从“谁有动机”转向“谁安排了黑暗”，局势开始收紧。");
                queueSceneCue(session, SceneCueType.REACTION, "反应", "提到停电时，几个人的反应几乎不是恐惧，而更像条件反射般的回避。");
            } else {
                session.setCurrentEnvironmentSummary("空气里开始出现焦躁的停顿，像每一句解释都需要先掂量后果。");
                session.setCurrentStoryBeat("表面的互相质疑正在变成更具体的追责，真正的破口快出现了。");
            }
            return;
        }

        session.setCurrentEnvironmentSummary("桌面上的证据越来越难被轻轻带过，连最克制的人也开始出现呼吸和语速的失衡。");
        session.setCurrentStoryBeat("现场已逼近指认时刻，剩下的不再是闲谈，而是谁会先扛不住最后一轮追问。");
        queueSceneCue(session, SceneCueType.FORESHADOW, "逼近", "线索已经压到桌面中央，谁先再开口，谁就可能把自己彻底暴露出来。");
    }

    private void queueSceneCue(GameSession session, SceneCueType type, String title, String content) {
        if (content == null || content.isBlank()) {
            return;
        }

        List<SceneCue> pendingCues = session.getPendingSceneCues();
        if (!pendingCues.isEmpty()) {
            SceneCue lastCue = pendingCues.get(pendingCues.size() - 1);
            if (lastCue.type() == type && lastCue.content().equals(content)) {
                return;
            }
        }
        pendingCues.add(new SceneCue(type, title, content));
    }

    private boolean containsAny(String source, String... fragments) {
        for (String fragment : fragments) {
            if (source.contains(fragment)) {
                return true;
            }
        }
        return false;
    }
}
