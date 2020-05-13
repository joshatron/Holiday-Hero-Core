package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.GiftIdea;
import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Wishlist {
    private List<GiftIdea> ideas;

    public Wishlist(String id, String owner) {
        ideas = new ArrayList<>();
    }

    public void addIdea(GiftIdea idea) {
        ideas.add(idea);
    }

    public List<GiftIdea> getWishlist() {
        return ideas;
    }

    public boolean wishlistContainsIdea(GiftIdea idea) {
        return ideas.contains(idea);
    }

    public void removeIdea(String toRemove) {
        ideas = ideas.stream()
                .filter(i -> !i.getId().equals(toRemove))
                .collect(Collectors.toList());
    }

    public GiftIdea findIdea(String idea) {
        Optional<GiftIdea> ideaOptional = ideas.stream()
                .filter(i -> i.getId().equals(idea))
                .findFirst();

        if(ideaOptional.isPresent()) {
            return ideaOptional.get();
        }

        throw new WishlistException(WishlistExceptionReason.IDEA_NOT_FOUND);
    }
}
