package io.joshatron.holiday.core.list;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class GenericItem {
    private String id;
    private String name;

    public GenericItem(String id) {
        this.id = id;
    }
}
