package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;

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
}
