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
     * 角色的公开背景。
     */
    private String publicBackstory;

    /**
     * 角色的私密背景。
     */
    private String privateBackstory;

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
     * 角色的公开目标。
     */
    private String publicObjective;

    /**
     * 角色的私密目标。
     */
    private String privateObjective;

    /**
     * 给玩家阅读的开局提示。
     */
    private String openingTip;

    /**
     * 角色回答策略说明。
     */
    private String responseStrategy;

    /**
     * 当前角色是否允许被玩家扮演。
     */
    private boolean selectableByPlayer;

    /**
     * 是否为真凶。
     */
    private boolean killer;

    /**
     * 是否为帮凶。
     */
    private boolean accomplice;

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

    public String getPublicBackstory() {
        return publicBackstory;
    }

    public void setPublicBackstory(String publicBackstory) {
        this.publicBackstory = publicBackstory;
    }

    public String getPrivateBackstory() {
        return privateBackstory;
    }

    public void setPrivateBackstory(String privateBackstory) {
        this.privateBackstory = privateBackstory;
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

    public String getPublicObjective() {
        return publicObjective;
    }

    public void setPublicObjective(String publicObjective) {
        this.publicObjective = publicObjective;
    }

    public String getPrivateObjective() {
        return privateObjective;
    }

    public void setPrivateObjective(String privateObjective) {
        this.privateObjective = privateObjective;
    }

    public String getOpeningTip() {
        return openingTip;
    }

    public void setOpeningTip(String openingTip) {
        this.openingTip = openingTip;
    }

    public String getResponseStrategy() {
        return responseStrategy;
    }

    public void setResponseStrategy(String responseStrategy) {
        this.responseStrategy = responseStrategy;
    }

    public boolean isSelectableByPlayer() {
        return selectableByPlayer;
    }

    public void setSelectableByPlayer(boolean selectableByPlayer) {
        this.selectableByPlayer = selectableByPlayer;
    }

    public boolean isKiller() {
        return killer;
    }

    public void setKiller(boolean killer) {
        this.killer = killer;
    }

    public boolean isAccomplice() {
        return accomplice;
    }

    public void setAccomplice(boolean accomplice) {
        this.accomplice = accomplice;
    }
}
