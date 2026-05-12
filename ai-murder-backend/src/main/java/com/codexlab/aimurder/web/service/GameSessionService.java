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

@Service
public class GameSessionService {

    private final Map<String, GameSession> sessionStore = new ConcurrentHashMap<>();
    private final ScriptRepository scriptRepository;

    public GameSessionService(ScriptRepository scriptRepository) {
        this.scriptRepository = scriptRepository;
    }

    public GameSession getOrCreate(String sessionId) {
        return sessionStore.computeIfAbsent(sessionId, key -> {
            ScriptDefinition scriptDefinition = scriptRepository.getDefaultScript();
            CharacterDefinition playerCharacter = getDefaultPlayerCharacter(scriptDefinition);
            return createSession(key, scriptDefinition, playerCharacter);
        });
    }

    public GameSession initializeSession(String sessionId, String scriptId, String playerCharacterId) {
        ScriptDefinition scriptDefinition = requireScript(scriptId);
        CharacterDefinition playerCharacter = requirePlayerCharacter(scriptDefinition, playerCharacterId);
        GameSession session = createSession(sessionId, scriptDefinition, playerCharacter);
        sessionStore.put(sessionId, session);
        return session;
    }

    public GameSession getExisting(String sessionId) {
        GameSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }
        return session;
    }

    public void appendMessage(String sessionId, String role, String content) {
        GameSession session = getOrCreate(sessionId);
        session.getMessageHistory().add(new ChatContextMessage(role, content));
        session.setUpdatedAt(LocalDateTime.now());
    }

    public List<ChatContextMessage> getMessageHistory(String sessionId) {
        return new ArrayList<>(getOrCreate(sessionId).getMessageHistory());
    }

    public boolean isOpeningDelivered(String sessionId) {
        return getOrCreate(sessionId).isOpeningDelivered();
    }

    public void markOpeningDelivered(String sessionId) {
        GameSession session = getOrCreate(sessionId);
        session.setOpeningDelivered(true);
        session.setUpdatedAt(LocalDateTime.now());
    }

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

    public List<ClueDefinition> consumePendingClues(String sessionId) {
        GameSession session = getOrCreate(sessionId);
        if (session.getPendingClueIds().isEmpty()) {
            return List.of();
        }

        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
        List<ClueDefinition> clues = scriptDefinition.getClues().stream()
                .filter(clue -> session.getPendingClueIds().contains(clue.getClueId()))
                .toList();
        session.getPendingClueIds().clear();
        session.setUpdatedAt(LocalDateTime.now());
        return clues;
    }

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

    public ChatStreamProgressResponse buildProgress(String sessionId) {
        GameSession session = getOrCreate(sessionId);
        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
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

        String playerDisplay = session.getPlayerCharacterName() == null
                ? scriptDefinition.getPlayerModeName()
                : session.getPlayerCharacterName() + " / " + session.getPlayerIdentity();

        return new ChatStreamProgressResponse(
                scriptDefinition.getScriptName(),
                playerDisplay,
                stageDefinition == null ? "" : stageDefinition.getStageName(),
                stageDefinition == null ? 0 : stageDefinition.getStageOrder(),
                scriptDefinition.getStages().size(),
                stageDefinition == null ? "" : stageDefinition.getObjective(),
                defaultText(session.getCurrentEnvironmentSummary(), scriptDefinition.getOpeningNarration()),
                defaultText(session.getCurrentStoryBeat(), "局面仍在试探期。"),
                session.getPlayerTurnCount(),
                revealedClues
        );
    }

    public List<ClueDefinition> getAvailableClues(GameSession session) {
        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
        if (session.getCurrentStage() == null) {
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

    public CharacterDefinition getSpeaker(GameSession session, String speakerId) {
        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
        return scriptDefinition.getCharacters().stream()
                .filter(character -> character.getCharacterId().equals(speakerId))
                .findFirst()
                .orElseGet(() -> getHostCharacter(scriptDefinition));
    }

    public StageDefinition getCurrentStageDefinition(GameSession session) {
        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
        if (session.getCurrentStage() == null) {
            return null;
        }

        return scriptDefinition.getStages().stream()
                .filter(stage -> stage.getStageId().equals(session.getCurrentStage().getStageId()))
                .findFirst()
                .orElse(null);
    }

    public CharacterDefinition getPlayerCharacter(GameSession session) {
        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
        return scriptDefinition.getCharacters().stream()
                .filter(character -> character.getCharacterId().equals(session.getPlayerCharacterId()))
                .findFirst()
                .orElse(null);
    }

    public CharacterDefinition getHostCharacter(GameSession session) {
        return getHostCharacter(requireScript(session.getScriptId()));
    }

    public boolean isInPrologue(GameSession session) {
        return session.getPlayerTurnCount() <= 1;
    }

    public boolean isStageJustChanged(GameSession session) {
        return session.isStageJustChanged();
    }

    public String getEnvironmentSummary(GameSession session) {
        return session.getCurrentEnvironmentSummary();
    }

    public String getStoryBeat(GameSession session) {
        return session.getCurrentStoryBeat();
    }

    private GameSession createSession(String sessionId, ScriptDefinition scriptDefinition, CharacterDefinition playerCharacter) {
        GameSession session = new GameSession();
        session.setSessionId(sessionId);
        session.setScriptId(scriptDefinition.getScriptId());
        session.setHostCharacterId(scriptDefinition.getHostCharacterId());
        session.setPlayerCharacterId(playerCharacter.getCharacterId());
        session.setPlayerCharacterName(playerCharacter.getCharacterName());
        session.setPlayerIdentity(playerCharacter.getIdentity());
        session.setPlayerRoleDescription(buildPlayerRoleDescription(playerCharacter));
        session.setPlayerObjective(defaultText(playerCharacter.getPrivateObjective(), playerCharacter.getPublicObjective()));
        session.setOpeningDelivered(true);
        session.setStatus(GameSessionStatus.IN_PROGRESS);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        session.setCurrentStage(buildInitialStage(scriptDefinition));
        session.setCharacterStates(buildCharacterStates(scriptDefinition, playerCharacter.getCharacterId()));
        session.setClueStates(buildClueStates(scriptDefinition));
        session.setPendingClueIds(new ArrayList<>());
        session.setPendingSceneCues(new ArrayList<>());
        session.setPlayerTurnCount(0);
        session.setStageTurnCount(0);
        session.setStageJustChanged(false);
        session.setCurrentEnvironmentSummary(scriptDefinition.getOpeningNarration());
        session.setCurrentStoryBeat("所有人刚刚入座，真正的破绽还藏在呼吸和停顿里。");
        queueSceneCue(session, SceneCueType.ENTRY, "入局", scriptDefinition.getOpeningNarration());
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

    private List<CharacterSessionState> buildCharacterStates(ScriptDefinition scriptDefinition, String playerCharacterId) {
        return scriptDefinition.getCharacters().stream()
                .filter(character -> !character.getCharacterId().equals(scriptDefinition.getHostCharacterId()))
                .filter(character -> !character.getCharacterId().equals(playerCharacterId))
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
        String normalizedMessage = defaultText(message, "");
        session.getCharacterStates().forEach(state -> {
            if (state.getCharacterId().equals(speakerId)) {
                state.setPressureLevel(state.getPressureLevel() + 1);
            }

            if (state.getCharacterId().equals(speakerId)
                    && containsAny(normalizedMessage, "怀疑", "是不是你", "凶手", "你杀", "帮凶")) {
                state.setSuspected(true);
            }

            if (state.getPressureLevel() >= 3) {
                state.setLoosened(true);
            }
        });
    }

    private void updateStageState(GameSession session, String message) {
        SessionStageState currentStage = session.getCurrentStage();
        if (currentStage == null) {
            return;
        }

        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
        int currentOrder = currentStage.getStageOrder();
        StageDefinition stageDefinition = scriptDefinition.getStages().stream()
                .filter(stage -> stage.getStageOrder() == currentOrder)
                .findFirst()
                .orElse(null);

        if (stageDefinition == null || currentOrder >= scriptDefinition.getStages().size()) {
            return;
        }

        boolean reachedMinTurns = session.getStageTurnCount() >= stageDefinition.getMinimumTurnsBeforeAdvance();
        boolean keywordTriggered = containsAny(defaultText(message, ""), stageDefinition.getAdvanceKeywords().toArray(String[]::new));
        boolean pressureTriggered = session.getCharacterStates().stream().anyMatch(state -> state.getPressureLevel() >= 2);

        if (reachedMinTurns && (keywordTriggered || pressureTriggered)) {
            session.setCurrentStage(buildStageState(session.getScriptId(), currentOrder + 1));
        }
    }

    private SessionStageState buildStageState(String scriptId, int stageOrder) {
        ScriptDefinition scriptDefinition = requireScript(scriptId);
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
        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
        StageDefinition currentStage = getCurrentStageDefinition(session);
        if (currentStage == null) {
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
        String normalizedMessage = defaultText(message, "");

        if (session.isStageJustChanged()) {
            session.setCurrentEnvironmentSummary(stage.getOpeningNarration());
            session.setCurrentStoryBeat("局面刚切入“" + stage.getStageName() + "”，桌面下的判断和站位都在重排。");
            queueSceneCue(session, SceneCueType.TRANSITION, "转场", stage.getOpeningNarration());
            return;
        }

        if (turnCount == 1) {
            session.setCurrentEnvironmentSummary("开场秩序还勉强维持着，但每个人都已经开始重新估量彼此的危险程度。");
            session.setCurrentStoryBeat("第一轮发问更像站位与试探，而不是立刻撕开真相。");
            queueSceneCue(session, SceneCueType.ENTRY, "试探", "第一句追问落下去之后，桌边所有人的停顿都比话语更有分量。");
            return;
        }

        if (stage.getStageOrder() == 1) {
            session.setCurrentEnvironmentSummary("表面的一致口供还没有崩，但真正的紧张已经从语气和余光里慢慢渗出来。");
            session.setCurrentStoryBeat("众人正在从“先把局面稳住”过渡到“开始辨认谁在回避”。");
            if (containsAny(normalizedMessage, "谁", "为什么", "最后", "当时")) {
                queueSceneCue(session, SceneCueType.PRESSURE, "压迫", "有人把问题问深了一寸，原本还能糊过去的沉默立刻变得危险。");
            }
            return;
        }

        if (stage.getStageOrder() == 2) {
            session.setCurrentEnvironmentSummary("调查重点已经从动机转向手法，几个人的回答开始出现刻意选择的痕迹。");
            session.setCurrentStoryBeat("真相不再藏在抽象怀疑里，而是在时间线和细节里逼近。");
            if (containsAny(normalizedMessage, "停电", "信", "账本", "配电箱", "旧案", "码头")) {
                queueSceneCue(session, SceneCueType.REACTION, "反应", "提到关键物证和旧事时，有人的反应不像惊讶，更像条件反射般地想绕开。");
            }
            return;
        }

        session.setCurrentEnvironmentSummary("桌上的证据已经足够把气氛压到发紧，所有辩解都像是在给最后一击争时间。");
        session.setCurrentStoryBeat("现场已经逼近指认时刻，剩下的不是闲谈，而是谁会先露出撑不住的那一下。");
        queueSceneCue(session, SceneCueType.FORESHADOW, "逼近", "局面已经走到不能后退的位置，再多一句追问，就可能把某个人彻底推到灯下。");
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

    private CharacterDefinition requirePlayerCharacter(ScriptDefinition scriptDefinition, String playerCharacterId) {
        return scriptDefinition.getCharacters().stream()
                .filter(CharacterDefinition::isSelectableByPlayer)
                .filter(character -> character.getCharacterId().equals(playerCharacterId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("玩家角色不存在或不可扮演: " + playerCharacterId));
    }

    private CharacterDefinition getDefaultPlayerCharacter(ScriptDefinition scriptDefinition) {
        return scriptDefinition.getCharacters().stream()
                .filter(CharacterDefinition::isSelectableByPlayer)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("剧本未配置可扮演角色: " + scriptDefinition.getScriptId()));
    }

    private CharacterDefinition getHostCharacter(ScriptDefinition scriptDefinition) {
        return scriptDefinition.getCharacters().stream()
                .filter(character -> character.getCharacterId().equals(scriptDefinition.getHostCharacterId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("剧本未配置控场角色: " + scriptDefinition.getScriptId()));
    }

    private ScriptDefinition requireScript(String scriptId) {
        ScriptDefinition scriptDefinition = scriptRepository.findById(scriptId);
        if (scriptDefinition == null) {
            throw new IllegalArgumentException("剧本不存在: " + scriptId);
        }
        return scriptDefinition;
    }

    private String buildPlayerRoleDescription(CharacterDefinition playerCharacter) {
        List<String> sections = new ArrayList<>();
        sections.add("身份：" + defaultText(playerCharacter.getIdentity(), "未知"));
        sections.add("关系：" + defaultText(playerCharacter.getRelationship(), "暂无"));
        sections.add("表面人设：" + defaultText(playerCharacter.getPublicPersona(), "暂无"));
        sections.add("公开背景：" + defaultText(playerCharacter.getPublicBackstory(), "暂无"));
        if (playerCharacter.getPrivateBackstory() != null && !playerCharacter.getPrivateBackstory().isBlank()) {
            sections.add("隐秘背景：" + playerCharacter.getPrivateBackstory());
        }
        if (playerCharacter.getOpeningTip() != null && !playerCharacter.getOpeningTip().isBlank()) {
            sections.add("开局提醒：" + playerCharacter.getOpeningTip());
        }
        return String.join("\n", sections);
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private boolean containsAny(String source, String... fragments) {
        if (source == null || source.isBlank() || fragments == null) {
            return false;
        }
        for (String fragment : fragments) {
            if (fragment != null && !fragment.isBlank() && source.contains(fragment)) {
                return true;
            }
        }
        return false;
    }
}
