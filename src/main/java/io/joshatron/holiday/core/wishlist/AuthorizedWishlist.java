package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;

import java.util.List;

public class AuthorizedWishlist {
    Wishlist wishlist;

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
}
