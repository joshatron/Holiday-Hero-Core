package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.WishlistIdea;
import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Wishlist {
    private String owner;
    private List<WishlistIdea> ideas;

    public Wishlist(String id, String owner) {
        this.owner = owner;
        ideas = new ArrayList<>();
    }

    public void addIdea(WishlistIdea idea) {
        ideas.add(idea);
    }

    public List<WishlistIdea> getWishlist() {
        return ideas;
    }

    public boolean containsIdea(String idea) {
        return ideas.stream()
                .anyMatch(i -> i.getId().equals(idea));
    }

    public void removeIdea(String toRemove) {
        ideas = ideas.stream()
                .filter(i -> !i.getId().equals(toRemove))
                .collect(Collectors.toList());
    }

    public WishlistIdea findIdea(String idea) {
        Optional<WishlistIdea> ideaOptional = ideas.stream()
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

    public String getOwner() {
        return owner;
    }
}
