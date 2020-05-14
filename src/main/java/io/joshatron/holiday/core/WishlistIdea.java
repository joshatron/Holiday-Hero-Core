package io.joshatron.holiday.core;

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

    public boolean isClaimed() {
        return !claimer.isEmpty();
    }
}
