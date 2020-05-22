package io.joshatron.holiday.core.proposedlist;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@EqualsAndHashCode
@ToString
public class ProposedList {
    private String id;
    private String owner;
    private ArrayList<ProposedIdea> list;

    public ProposedList(String id, String owner) {
        this.id = id;
        this.owner = owner;
        this.list = new ArrayList<>();
    }
}
