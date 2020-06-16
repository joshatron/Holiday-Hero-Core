package io.joshatron.holiday.core.list;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenericList<G extends GenericItem> {
    private String id;
    private String owner;
    private List<G> list;
    
    public GenericList(String id, String owner) {
        this.id = id;
        this.owner = owner;
        this.list = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public List<G> getList() {
        return list;
    }

    public void addItem(G item) {
        if(containsItem(item.getId())) {
            throw new ListException(ListExceptionReason.ITEM_ALREADY_ADDED);
        }

        list.add(item);
    }

    public boolean containsItem(String item) {
        return list.stream()
                .anyMatch(i -> i.getId().equals(item));
    }

    public G getItem(String item) {
        Optional<G> found = list.stream()
                .filter(i -> i.getId().equals(item))
                .findFirst();

        if(!found.isPresent()) {
            throw new ListException(ListExceptionReason.ITEM_NOT_FOUND);
        }

        return found.get();
    }

    public void removeItem(String item) {
        if(!containsItem(item)) {
            throw new ListException(ListExceptionReason.ITEM_NOT_FOUND);
        }

        list = list.stream()
                .filter(i -> !i.getId().equals(item))
                .collect(Collectors.toList());
    }

    public void updateItem(G replacement) {
        if(!containsItem(replacement.getId())) {
            throw new ListException(ListExceptionReason.ITEM_NOT_FOUND);
        }

        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getId().equals(replacement.getId())) {
                list.set(i, replacement);
                return;
            }
        }
    }
}
