package com.codexlab.aimurder.domain.session.model;

import java.time.LocalDateTime;

/**
 * 线索公开状态。
 * 用于记录某条线索在本局中是否已经对玩家公开。
 */
public class ClueRevealState {

    /**
     * 线索标识。
     */
    private String clueId;

    /**
     * 是否已公开。
     */
    private boolean revealed;

    /**
     * 公开时间。
     */
    private LocalDateTime revealedAt;

    public String getClueId() {
        return clueId;
    }

    public void setClueId(String clueId) {
        this.clueId = clueId;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public LocalDateTime getRevealedAt() {
        return revealedAt;
    }

    public void setRevealedAt(LocalDateTime revealedAt) {
        this.revealedAt = revealedAt;
    }
}
