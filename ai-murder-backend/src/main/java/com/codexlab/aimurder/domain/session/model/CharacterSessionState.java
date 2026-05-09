package com.codexlab.aimurder.domain.session.model;

/**
 * 角色运行时状态。
 * 用于记录某个角色在本局中的情绪、压力和是否已被重点怀疑。
 */
public class CharacterSessionState {

    /**
     * 角色标识。
     */
    private String characterId;

    /**
     * 是否已被玩家重点怀疑。
     */
    private boolean suspected;

    /**
     * 当前心理压力值，可用于影响后续回复风格。
     */
    private int pressureLevel;

    /**
     * 是否已经松口。
     */
    private boolean loosened;

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public boolean isSuspected() {
        return suspected;
    }

    public void setSuspected(boolean suspected) {
        this.suspected = suspected;
    }

    public int getPressureLevel() {
        return pressureLevel;
    }

    public void setPressureLevel(int pressureLevel) {
        this.pressureLevel = pressureLevel;
    }

    public boolean isLoosened() {
        return loosened;
    }

    public void setLoosened(boolean loosened) {
        this.loosened = loosened;
    }
}
