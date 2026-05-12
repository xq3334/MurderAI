package com.codexlab.aimurder.web.service;

import com.codexlab.aimurder.domain.script.definition.CharacterDefinition;
import com.codexlab.aimurder.domain.script.definition.ScriptDefinition;
import com.codexlab.aimurder.domain.session.model.GameSession;
import com.codexlab.aimurder.web.dto.PlayerRoleCardResponse;
import com.codexlab.aimurder.web.dto.ScriptSummaryResponse;
import com.codexlab.aimurder.web.dto.SessionBootstrapResponse;
import com.codexlab.aimurder.web.dto.SessionCharacterSeatResponse;
import com.codexlab.aimurder.web.dto.SessionDetailResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

/**
 * 副本目录服务。
 * 负责列出副本、返回角色卡，以及初始化带角色身份的会话。
 */
@Service
public class ScriptCatalogService {

    private final ScriptRepository scriptRepository;
    private final GameSessionService gameSessionService;

    public ScriptCatalogService(ScriptRepository scriptRepository, GameSessionService gameSessionService) {
        this.scriptRepository = scriptRepository;
        this.gameSessionService = gameSessionService;
    }

    /**
     * 列出全部可选副本。
     *
     * @return 副本摘要列表
     */
    public List<ScriptSummaryResponse> listScripts() {
        return scriptRepository.findAll().stream()
                .map(script -> new ScriptSummaryResponse(
                        script.getScriptId(),
                        script.getScriptName(),
                        script.getSummary(),
                        script.getOpeningNarration(),
                        script.getPlayerModeName(),
                        (int) script.getCharacters().stream().filter(CharacterDefinition::isSelectableByPlayer).count(),
                        script.getUnlockOrder(),
                        script.isRandomRoleOnStart()
                ))
                .toList();
    }

    /**
     * 查询某个副本下可供玩家选择的角色卡。
     *
     * @param scriptId 副本标识
     * @return 角色卡列表
     */
    public List<PlayerRoleCardResponse> listPlayerRoles(String scriptId) {
        ScriptDefinition scriptDefinition = requireScript(scriptId);
        return scriptDefinition.getCharacters().stream()
                .filter(CharacterDefinition::isSelectableByPlayer)
                .map(this::toRoleCard)
                .toList();
    }

    /**
     * 初始化一局带角色卡的会话。
     *
     * @param sessionId 会话标识，可为空
     * @param scriptId 副本标识
     * @param playerCharacterId 玩家角色标识
     * @return 初始化结果
     */
    public SessionBootstrapResponse initializeSession(String sessionId, String scriptId, String playerCharacterId) {
        String resolvedSessionId = (sessionId == null || sessionId.isBlank())
                ? "session-" + UUID.randomUUID().toString().substring(0, 8)
                : sessionId;
        GameSession session = gameSessionService.initializeSession(resolvedSessionId, scriptId, playerCharacterId);
        ScriptDefinition scriptDefinition = requireScript(scriptId);
        return new SessionBootstrapResponse(
                session.getSessionId(),
                scriptDefinition.getScriptId(),
                scriptDefinition.getScriptName(),
                session.getPlayerCharacterId(),
                session.getPlayerCharacterName(),
                session.getPlayerIdentity(),
                session.getPlayerRoleDescription(),
                session.getPlayerObjective(),
                scriptDefinition.getOpeningNarration()
        );
    }

    /**
     * 为指定副本随机抽取一张可扮演角色卡并初始化会话。
     *
     * @param sessionId 会话标识，可为空
     * @param scriptId 副本标识
     * @return 初始化结果
     */
    public SessionBootstrapResponse initializeSessionWithRandomRole(String sessionId, String scriptId) {
        ScriptDefinition scriptDefinition = requireScript(scriptId);
        List<CharacterDefinition> selectableCharacters = scriptDefinition.getCharacters().stream()
                .filter(CharacterDefinition::isSelectableByPlayer)
                .toList();
        if (selectableCharacters.isEmpty()) {
            throw new IllegalArgumentException("副本下没有可抽取的玩家角色: " + scriptId);
        }

        CharacterDefinition selectedCharacter = selectableCharacters.get(
                ThreadLocalRandom.current().nextInt(selectableCharacters.size())
        );
        return initializeSession(sessionId, scriptId, selectedCharacter.getCharacterId());
    }

    /**
     * 查询当前会话详情，供前端恢复局状态。
     *
     * @param sessionId 会话标识
     * @return 会话详情
     */
    public SessionDetailResponse getSessionDetail(String sessionId) {
        GameSession session = gameSessionService.getExisting(sessionId);
        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());
        return new SessionDetailResponse(
                session.getSessionId(),
                scriptDefinition.getScriptId(),
                scriptDefinition.getScriptName(),
                session.getPlayerCharacterId(),
                session.getPlayerCharacterName(),
                session.getPlayerIdentity(),
                session.getPlayerRoleDescription(),
                session.getPlayerObjective(),
                session.isOpeningDelivered(),
                buildCharacterSeats(scriptDefinition, session),
                gameSessionService.buildProgress(sessionId)
        );
    }

    private List<SessionCharacterSeatResponse> buildCharacterSeats(ScriptDefinition scriptDefinition, GameSession session) {
        return scriptDefinition.getCharacters().stream()
                .filter(character -> !character.getCharacterId().equals(scriptDefinition.getHostCharacterId()))
                .filter(character -> !character.getCharacterId().equals(session.getPlayerCharacterId()))
                .map(character -> new SessionCharacterSeatResponse(
                        character.getCharacterId(),
                        character.getCharacterName(),
                        character.getIdentity(),
                        defaultText(character.getPublicPersona(), "神色克制，暂未露出口风"),
                        defaultText(character.getResponseStrategy(), "还没有露出真正的回答节奏")
                ))
                .toList();
    }

    private ScriptDefinition requireScript(String scriptId) {
        ScriptDefinition scriptDefinition = scriptRepository.findById(scriptId);
        if (scriptDefinition == null) {
            throw new IllegalArgumentException("副本不存在: " + scriptId);
        }
        return scriptDefinition;
    }

    private PlayerRoleCardResponse toRoleCard(CharacterDefinition character) {
        return new PlayerRoleCardResponse(
                character.getCharacterId(),
                character.getCharacterName(),
                character.getIdentity(),
                character.getRelationship(),
                character.getPublicPersona(),
                character.getPublicBackstory(),
                character.getPublicObjective()
        );
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
