package io.joshatron.holiday.core;

import lombok.Data;

@Data
public class GiftIdeaAndStatus {
    private GiftIdea idea;
    private Person claimedBy;

    public GiftIdeaAndStatus(GiftIdea idea) {
        this.idea = idea;
        this.claimedBy = null;
    }

    public boolean isClaimed() {
        return claimedBy != null;
    }

    public void removeClaim() {
        claimedBy = null;
    }
}
