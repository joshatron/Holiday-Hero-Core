package io.joshatron.holiday.core.list.proposed;

import lombok.Data;

@Data
public class ProposedIdea {
    private String id;
    private String name;

    public ProposedIdea(String id) {
        this.id = id;
        this.name = "";
    }
}
