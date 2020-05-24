package io.joshatron.holiday.core.proposedlist;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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

    public void addIdea(ProposedIdea idea) {
        list.add(idea);
    }

    public boolean containsIdea(String idea) {
        return list.stream()
                .anyMatch(i -> i.getId().equals(idea));
    }

    public void addIdeas(List<ProposedIdea> ideas) {
        ideas.forEach(this::addIdea);
    }
}
