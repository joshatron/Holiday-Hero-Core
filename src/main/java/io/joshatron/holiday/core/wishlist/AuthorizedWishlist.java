package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorizedWishlist {
    private Wishlist wishlist;

    public AuthorizedWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public void addIdea(String user, WishlistIdea idea) {
        if(!wishlist.getOwner().equals(user)) {
            throw new WishlistException(WishlistExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.addIdea(idea);
    }

    public boolean containsIdea(String user, String idea) {
        return wishlist.containsIdea(idea);
    }

    public WishlistIdea getIdea(String user, String idea) {
        WishlistIdea foundIdea = new WishlistIdea(wishlist.getIdea(idea));

        if(wishlist.getOwner().equals(user)) {
            foundIdea.unclaim();
        }

        return foundIdea;
    }

    public void removeIdea(String user, String idea) {
        if(!wishlist.getOwner().equals(user)) {
            throw new WishlistException(WishlistExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.removeIdea(idea);
    }

    public void claimIdea(String user, String idea) {
        if(wishlist.getOwner().equals(user)) {
            throw new WishlistException(WishlistExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.claimIdea(user, idea);
    }

    public void unclaimIdea(String user, String idea) {
        if(!wishlist.getIdea(idea).getClaimer().equals(user)) {
            throw new WishlistException(WishlistExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.unclaimIdea(idea);
    }

    public List<WishlistIdea> rollover(String user) {
        if(!wishlist.getOwner().equals(user)) {
            throw new WishlistException(WishlistExceptionReason.USER_NOT_AUTHORIZED);
        }

        return wishlist.rollover();
    }

    public String getId(String user) {
        return wishlist.getId();
    }

    public List<WishlistIdea> getIdeas(String user) {
        List<WishlistIdea> ideas = wishlist.getList().stream()
                .map(WishlistIdea::new)
                .collect(Collectors.toList());

        if(wishlist.getOwner().equals(user)) {
            ideas.forEach(WishlistIdea::unclaim);
        }

        return ideas;
    }
}
