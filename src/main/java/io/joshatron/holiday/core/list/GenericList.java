package io.joshatron.holiday.core.list;

import java.util.ArrayList;
import java.util.List;

public class GenericList<G extends GenericItem> {
    private String id;
    private String owner;
    
    public GenericList(String id, String owner) {
        this.id = id;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public List<G> getList() {
        return new ArrayList<>();
    }
}
