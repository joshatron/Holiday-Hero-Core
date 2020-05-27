package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@ToString
public class Wishlist {
    private final String id;
    private final String owner;
    private List<WishlistIdea> list;

    public Wishlist(String id, String owner) {
        this.id = id;
        this.owner = owner;
        list = new ArrayList<>();
    }

    public void addIdea(WishlistIdea idea) {
        if(containsIdea(idea.getId())) {
            throw new WishlistException(WishlistExceptionReason.IDEA_ALREADY_ADDED);
        }

        list.add(idea);
    }

    public boolean containsIdea(String idea) {
        return list.stream()
                .anyMatch(i -> i.getId().equals(idea));
    }

    public WishlistIdea getIdea(String idea) {
        Optional<WishlistIdea> ideaOptional = list.stream()
                .filter(i -> i.getId().equals(idea))
                .findFirst();

        if(ideaOptional.isPresent()) {
            return ideaOptional.get();
        }

        throw new WishlistException(WishlistExceptionReason.IDEA_NOT_FOUND);
    }

    public void removeIdea(String toRemove) {
        if(claimingStarted()) {
            throw new WishlistException(WishlistExceptionReason.CLAIMING_STARTED);
        }
        if(!containsIdea(toRemove)) {
            throw new WishlistException(WishlistExceptionReason.IDEA_NOT_FOUND);
        }

        list = list.stream()
                .filter(i -> !i.getId().equals(toRemove))
                .collect(Collectors.toList());
    }

    private boolean claimingStarted() {
        return list.stream()
                .anyMatch(WishlistIdea::isClaimed);
    }

    public void claimIdea(String claimer, String ideaToClaim) {
        if(claimer.equals(owner)) {
            throw new WishlistException(WishlistExceptionReason.CANT_CLAIM_OWN);
        }

        WishlistIdea idea = getIdea(ideaToClaim);

        if(idea.isClaimed()) {
            throw new WishlistException(WishlistExceptionReason.ALREADY_CLAIMED);
        }

        idea.setClaimer(claimer);
    }

    public void unclaimIdea(String idea) {
        WishlistIdea foundIdea = getIdea(idea);
        if(!foundIdea.isClaimed()) {
            throw new WishlistException(WishlistExceptionReason.IDEA_NOT_CLAIMED);
        }

        foundIdea.unclaim();
    }

    public List<WishlistIdea> rollover() {
        List<WishlistIdea> rolledOver = list.stream()
                .filter(WishlistIdea::isClaimed)
                .collect(Collectors.toList());

        list = list.stream()
                .filter(i -> !i.isClaimed())
                .collect(Collectors.toList());

        return rolledOver;
    }

    public void updateIdea(WishlistIdea idea) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getId().equals(idea.getId())) {
                idea.setClaimer(list.get(i).getClaimer());
                list.set(i, idea);
                return;
            }
        }

        throw new WishlistException(WishlistExceptionReason.IDEA_NOT_FOUND);
    }
}
