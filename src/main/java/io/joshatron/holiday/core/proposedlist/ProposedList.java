package io.joshatron.holiday.core.proposedlist;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class ProposedList {
    private final String id;
    private final String owner;
    private final ArrayList<ProposedIdea> list;

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
}
