package io.joshatron.holiday.core.list.proposed;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
import io.joshatron.holiday.core.list.GenericList;

public class ProposedList extends GenericList<ProposedIdea> {
    public ProposedList(String id, String owner) {
        super(id, owner);
    }

    public ProposedIdea acceptItem(String id) {
        ProposedIdea idea = getItem(id);
        removeItem(id);

        return idea;
    }

    public void denyItem(String id) {
        if(!containsItem(id)) {
            throw new ListException(ListExceptionReason.ITEM_NOT_FOUND);
        }

        removeItem(id);
    }
}
