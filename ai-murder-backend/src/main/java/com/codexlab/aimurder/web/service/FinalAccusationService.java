package com.codexlab.aimurder.web.service;

import com.codexlab.aimurder.domain.script.definition.CharacterDefinition;
import com.codexlab.aimurder.domain.script.definition.ClueDefinition;
import com.codexlab.aimurder.domain.script.definition.ScriptDefinition;
import com.codexlab.aimurder.domain.session.model.GameSession;
import com.codexlab.aimurder.web.dto.EndingRevealResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 最终指认服务。
 * 负责结案条件校验、真凶判定与结局揭晓数据组装。
 */
@Service
public class FinalAccusationService {

    private final GameSessionService gameSessionService;
    private final ScriptRepository scriptRepository;

    public FinalAccusationService(GameSessionService gameSessionService, ScriptRepository scriptRepository) {
        this.gameSessionService = gameSessionService;
        this.scriptRepository = scriptRepository;
    }

    /**
     * 提交最终指认并返回结局揭晓数据。
     *
     * @param sessionId 会话标识
     * @param accusedCharacterId 被指认角色标识
     * @param reasoning 玩家推理摘要
     * @return 结局揭晓数据
     */
    public EndingRevealResponse submitAccusation(String sessionId, String accusedCharacterId, String reasoning) {
        GameSession session = gameSessionService.getExisting(sessionId);
        ScriptDefinition scriptDefinition = requireScript(session.getScriptId());

        if (!isAccusationAllowed(session, scriptDefinition)) {
            throw new IllegalArgumentException("当前还不能提交最终指认，请先推进到最后阶段并解锁足够关键线索。");
        }

        CharacterDefinition accusedCharacter = requireAccusedCharacter(scriptDefinition, accusedCharacterId, session);
        CharacterDefinition killerCharacter = requireKillerCharacter(scriptDefinition);
        CharacterDefinition playerCharacter = gameSessionService.getPlayerCharacter(session);

        boolean accusationSuccess = accusedCharacter.getCharacterId().equals(killerCharacter.getCharacterId());
        String playerOutcome;

        if (playerCharacter != null && (playerCharacter.isKiller() || playerCharacter.isAccomplice())) {
            playerOutcome = accusationSuccess
                    ? "你没能把怀疑从真正的作案链上移开，这一局你的隐藏立场失败了。"
                    : "你成功让最终指认偏离了真正的作案链，这一局你的隐藏立场暂时成立。";
        } else {
            playerOutcome = accusationSuccess
                    ? "你完成了有效锁凶，推理链成立。"
                    : "你的指认没有命中真正的作案者，推理链还缺最后一环。";
        }

        session.setPlayerConclusion(defaultText(reasoning, accusedCharacter.getCharacterName()));

        return new EndingRevealResponse(
                session.getSessionId(),
                scriptDefinition.getScriptId(),
                scriptDefinition.getScriptName(),
                defaultText(scriptDefinition.getEndingTitle(), "真相揭晓"),
                accusationSuccess,
                true,
                accusationSuccess
                        ? "你的最终指认命中了真正的凶手。"
                        : "你的最终指认没有命中真正的凶手。",
                playerOutcome,
                accusedCharacter.getCharacterName(),
                killerCharacter.getCharacterName(),
                defaultText(reasoning, "玩家未填写额外推理摘要。"),
                defaultText(scriptDefinition.getEndingStory(), scriptDefinition.getTruthSummary()),
                buildKeyEvidence(scriptDefinition)
        );
    }

    private boolean isAccusationAllowed(GameSession session, ScriptDefinition scriptDefinition) {
        boolean finalStageReached = session.getCurrentStage() != null
                && session.getCurrentStage().getStageOrder() >= scriptDefinition.getStages().size();
        long revealedKeyClueCount = scriptDefinition.getClues().stream()
                .filter(ClueDefinition::isKeyClue)
                .filter(clue -> session.getClueStates().stream()
                        .anyMatch(state -> state.getClueId().equals(clue.getClueId()) && state.isRevealed()))
                .count();

        return finalStageReached && revealedKeyClueCount >= scriptDefinition.getMinimumKeyCluesForAccusation();
    }

    private List<String> buildKeyEvidence(ScriptDefinition scriptDefinition) {
        return scriptDefinition.getClues().stream()
                .filter(ClueDefinition::isKeyClue)
                .map(clue -> clue.getClueName() + "：" + clue.getContent())
                .toList();
    }

    private ScriptDefinition requireScript(String scriptId) {
        ScriptDefinition scriptDefinition = scriptRepository.findById(scriptId);
        if (scriptDefinition == null) {
            throw new IllegalArgumentException("副本不存在: " + scriptId);
        }
        return scriptDefinition;
    }

    private CharacterDefinition requireAccusedCharacter(
            ScriptDefinition scriptDefinition,
            String accusedCharacterId,
            GameSession session
    ) {
        return scriptDefinition.getCharacters().stream()
                .filter(character -> !character.getCharacterId().equals(scriptDefinition.getHostCharacterId()))
                .filter(character -> !character.getCharacterId().equals(session.getPlayerCharacterId()))
                .filter(character -> character.getCharacterId().equals(accusedCharacterId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("被指认角色不存在或当前不可指认: " + accusedCharacterId));
    }

    private CharacterDefinition requireKillerCharacter(ScriptDefinition scriptDefinition) {
        return scriptDefinition.getCharacters().stream()
                .filter(CharacterDefinition::isKiller)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("副本未配置真凶角色"));
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
