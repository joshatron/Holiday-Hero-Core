package io.joshatron.holiday.core;

import lombok.Data;

@Data
public class GiftIdeaAndStatus {
    private GiftIdea idea;
    private boolean claimed;
    private Person claimedBy;

    public GiftIdeaAndStatus(GiftIdea idea) {
        this.idea = idea;
        this.claimed = false;
    }
}
