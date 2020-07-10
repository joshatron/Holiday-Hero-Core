package io.joshatron.holiday.core.list.proposed;

import io.joshatron.holiday.core.list.GenericItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProposedIdea extends GenericItem {
    private String giver;

    public ProposedIdea(String id) {
        super(id);
    }
}
