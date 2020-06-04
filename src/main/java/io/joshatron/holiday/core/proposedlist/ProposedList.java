package io.joshatron.holiday.core.proposedlist;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@ToString
public class ProposedList {
    private final String id;
    private final String owner;
    private List<ProposedIdea> list;

    public ProposedList(String id, String owner) {
        this.id = id;
        this.owner = owner;
        this.list = new ArrayList<>();
    }

    public void addIdea(ProposedIdea idea) {
        if(containsIdea(idea.getId())) {
            throw new ListException(ListExceptionReason.ITEM_ALREADY_ADDED);
        }

        list.add(idea);
    }

    public boolean containsIdea(String idea) {
        return list.stream()
                .anyMatch(i -> i.getId().equals(idea));
    }

    public void addIdeas(List<ProposedIdea> ideas) {
        for(ProposedIdea idea : ideas) {
            try {
                addIdea(idea);
            } catch (ListException ignore) {}
        }
    }

    public ProposedIdea acceptIdea(String id) {
        ProposedIdea idea = getIdea(id);
        removeIdea(id);

        return idea;
    }

    public ProposedIdea getIdea(String id) {
        for(ProposedIdea idea : list) {
            if(idea.getId().equals(id)) {
                return idea;
            }
        }

        throw new ListException(ListExceptionReason.ITEM_NOT_FOUND);
    }

    private void removeIdea(String id) {
        list = list.stream()
                .filter(i -> !i.getId().equals(id))
                .collect(Collectors.toList());
    }

    public void denyIdea(String id) {
        if(!containsIdea(id)) {
            throw new ListException(ListExceptionReason.ITEM_NOT_FOUND);
        }

        removeIdea(id);
    }
}
