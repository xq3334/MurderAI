package com.codexlab.aimurder.domain.script.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色定义。
 * 用于描述角色的静态设定、口径边界和可公开信息范围。
 */
public class CharacterDefinition {

    /**
     * 角色唯一标识。
     */
    private String characterId;

    /**
     * 角色名称。
     */
    private String characterName;

    /**
     * 角色身份。
     */
    private String identity;

    /**
     * 与死者或案件核心人物的关系。
     */
    private String relationship;

    /**
     * 性格标签。
     */
    private List<String> personalityTags = new ArrayList<>();

    /**
     * 角色对外表现的人设描述。
     */
    private String publicPersona;

    /**
     * 角色知道的事实。
     */
    private List<String> knownFacts = new ArrayList<>();

    /**
     * 角色隐藏的秘密。
     */
    private List<String> hiddenSecrets = new ArrayList<>();

    /**
     * 角色在本阶段之前不得主动泄露的信息。
     */
    private List<String> forbiddenDisclosures = new ArrayList<>();

    /**
     * 角色回答策略说明。
     */
    private String responseStrategy;

    /**
     * 是否为真凶。
     */
    private boolean killer;

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public List<String> getPersonalityTags() {
        return personalityTags;
    }

    public void setPersonalityTags(List<String> personalityTags) {
        this.personalityTags = personalityTags;
    }

    public String getPublicPersona() {
        return publicPersona;
    }

    public void setPublicPersona(String publicPersona) {
        this.publicPersona = publicPersona;
    }

    public List<String> getKnownFacts() {
        return knownFacts;
    }

    public void setKnownFacts(List<String> knownFacts) {
        this.knownFacts = knownFacts;
    }

    public List<String> getHiddenSecrets() {
        return hiddenSecrets;
    }

    public void setHiddenSecrets(List<String> hiddenSecrets) {
        this.hiddenSecrets = hiddenSecrets;
    }

    public List<String> getForbiddenDisclosures() {
        return forbiddenDisclosures;
    }

    public void setForbiddenDisclosures(List<String> forbiddenDisclosures) {
        this.forbiddenDisclosures = forbiddenDisclosures;
    }

    public String getResponseStrategy() {
        return responseStrategy;
    }

    public void setResponseStrategy(String responseStrategy) {
        this.responseStrategy = responseStrategy;
    }

    public boolean isKiller() {
        return killer;
    }

    public void setKiller(boolean killer) {
        this.killer = killer;
    }
}
