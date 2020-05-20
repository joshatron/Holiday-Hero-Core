package io.joshatron.holiday.core.wishlist;

import lombok.Data;

@Data
public class WishlistIdea {
    private String id;
    private String name;
    private String claimer;

    public WishlistIdea(String id) {
        this.id = id;
        this.name = "";
        this.claimer = "";
    }

    public WishlistIdea(WishlistIdea other) {
        this.id = other.id;
        this.name = other.name;
        this.claimer = other.claimer;
    }

    public boolean isClaimed() {
        return !claimer.isEmpty();
    }

    public void unclaim() {
        claimer = "";
    }
}
