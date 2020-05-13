package io.joshatron.holiday.core;

import lombok.Data;

@Data
public class GiftIdea {
    private String id;
    private String name;

    public GiftIdea(String id) {
        this.id = id;
        this.name = "";
    }
}
