package io.joshatron.holiday.core.list.wishlist;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
import io.joshatron.holiday.core.list.GenericList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@ToString
public class Wishlist extends GenericList<WishlistIdea> {

    public Wishlist(String id, String owner) {
        super(id, owner);
    }

    @Override
    public void removeItem(String item) {
        if(claimingStarted()) {
            throw new ListException(ListExceptionReason.CLAIMING_STARTED);
        }

        super.removeItem(item);
    }

    @Override
    public void updateItem(WishlistIdea item) {
        String originalClaimer = getItem(item.getId()).getClaimer();
        item.setClaimer(originalClaimer);
        super.updateItem(item);
    }

    private boolean claimingStarted() {
        return getList().stream()
                .anyMatch(WishlistIdea::isClaimed);
    }

    public void claimItem(String claimer, String ideaToClaim) {
        if(claimer.equals(getOwner())) {
            throw new ListException(ListExceptionReason.CANT_CLAIM_OWN);
        }

        WishlistIdea idea = getItem(ideaToClaim);

        if(idea.isClaimed()) {
            throw new ListException(ListExceptionReason.ALREADY_CLAIMED);
        }

        idea.setClaimer(claimer);
    }

    public void unclaimItem(String idea) {
        WishlistIdea foundIdea = getItem(idea);
        if(!foundIdea.isClaimed()) {
            throw new ListException(ListExceptionReason.ITEM_NOT_CLAIMED);
        }

        foundIdea.unclaim();
    }

    public List<WishlistIdea> rollover() {
        List<WishlistIdea> rolledOver = getList().stream()
                .filter(WishlistIdea::isClaimed)
                .collect(Collectors.toList());

        setList(getList().stream()
                .filter(i -> !i.isClaimed())
                .collect(Collectors.toList()));

        return rolledOver;
    }
}
