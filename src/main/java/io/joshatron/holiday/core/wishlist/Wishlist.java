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
    private String id;
    private String owner;
    private List<WishlistIdea> wishlist;

    public Wishlist(String id, String owner) {
        this.id = id;
        this.owner = owner;
        wishlist = new ArrayList<>();
    }

    public void addIdea(WishlistIdea idea) {
        wishlist.add(idea);
    }

    public boolean containsIdea(String idea) {
        return wishlist.stream()
                .anyMatch(i -> i.getId().equals(idea));
    }

    public void removeIdea(String toRemove) {
        if(claimingStarted()) {
            throw new WishlistException(WishlistExceptionReason.CLAIMING_STARTED);
        }

        wishlist = wishlist.stream()
                .filter(i -> !i.getId().equals(toRemove))
                .collect(Collectors.toList());
    }

    private boolean claimingStarted() {
        return wishlist.stream()
                .anyMatch(WishlistIdea::isClaimed);
    }

    public WishlistIdea findIdea(String idea) {
        Optional<WishlistIdea> ideaOptional = wishlist.stream()
                .filter(i -> i.getId().equals(idea))
                .findFirst();

        if(ideaOptional.isPresent()) {
            return ideaOptional.get();
        }

        throw new WishlistException(WishlistExceptionReason.IDEA_NOT_FOUND);
    }

    public void claimIdea(String claimer, String ideaToClaim) {
        if(claimer.equals(owner)) {
            throw new WishlistException(WishlistExceptionReason.CANT_CLAIM_OWN);
        }

        WishlistIdea idea = findIdea(ideaToClaim);

        if(idea.isClaimed()) {
            throw new WishlistException(WishlistExceptionReason.ALREADY_CLAIMED);
        }

        idea.setClaimer(claimer);
    }

    public void unclaimIdea(String idea) {
        findIdea(idea).unclaim();
    }

    public List<WishlistIdea> rollover() {
        List<WishlistIdea> rolledOver = wishlist.stream()
                .filter(WishlistIdea::isClaimed)
                .collect(Collectors.toList());

        wishlist = wishlist.stream()
                .filter(i -> !i.isClaimed())
                .collect(Collectors.toList());

        return rolledOver;
    }
}
