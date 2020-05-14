package io.joshatron.holiday.core;

import lombok.Data;

@Data
public class GiftIdeaAndStatus {
    private WishlistIdea idea;
    private Person claimedBy;

    public GiftIdeaAndStatus(WishlistIdea idea) {
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
