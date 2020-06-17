package io.joshatron.holiday.core.list.wishlist;

import io.joshatron.holiday.core.list.GenericItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WishlistIdea extends GenericItem {
    private String claimer;

    public WishlistIdea(String id) {
        super(id);
        this.claimer = "";
    }

    public WishlistIdea(WishlistIdea other) {
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
