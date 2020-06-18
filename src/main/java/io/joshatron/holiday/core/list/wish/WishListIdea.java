package io.joshatron.holiday.core.list.wish;

import io.joshatron.holiday.core.list.GenericItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WishListIdea extends GenericItem {
    private String claimer;

    public WishListIdea(String id) {
        super(id);
        this.claimer = "";
    }

    public WishListIdea(WishListIdea other) {
        super(other.getId());
        setName(other.getName());
        setClaimer(other.getClaimer());
    }

    public boolean isClaimed() {
        return !claimer.isEmpty();
    }

    public void unclaim() {
        claimer = "";
    }
}
