package io.joshatron.holiday.core.list.received;

import io.joshatron.holiday.core.list.GenericItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReceivedItem extends GenericItem {
    private LocalDate received;
    private String giver;

    public ReceivedItem(String id) {
        super(id);
    }
}
