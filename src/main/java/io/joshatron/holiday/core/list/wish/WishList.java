package io.joshatron.holiday.core.list.wish;

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
public class WishList extends GenericList<WishListIdea> {

    public WishList(String id, String owner) {
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
    public void updateItem(WishListIdea item) {
        String originalClaimer = getItem(item.getId()).getClaimer();
        item.setClaimer(originalClaimer);
        super.updateItem(item);
    }

    private boolean claimingStarted() {
        return getList().stream()
                .anyMatch(WishListIdea::isClaimed);
    }

    public void claimItem(String claimer, String ideaToClaim) {
        if(claimer.equals(getOwner())) {
            throw new ListException(ListExceptionReason.CANT_CLAIM_OWN);
        }

        WishListIdea idea = getItem(ideaToClaim);

        if(idea.isClaimed()) {
            throw new ListException(ListExceptionReason.ALREADY_CLAIMED);
        }

        idea.setClaimer(claimer);
    }

    public void unclaimItem(String idea) {
        WishListIdea foundIdea = getItem(idea);
        if(!foundIdea.isClaimed()) {
            throw new ListException(ListExceptionReason.ITEM_NOT_CLAIMED);
        }

        foundIdea.unclaim();
    }

    public List<WishListIdea> rollover() {
        List<WishListIdea> rolledOver = getList().stream()
                .filter(WishListIdea::isClaimed)
                .collect(Collectors.toList());

        setList(getList().stream()
                .filter(i -> !i.isClaimed())
                .collect(Collectors.toList()));

        return rolledOver;
    }
}
