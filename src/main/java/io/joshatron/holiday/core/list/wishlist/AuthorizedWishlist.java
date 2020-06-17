package io.joshatron.holiday.core.list.wishlist;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorizedWishlist {
    private Wishlist wishlist;

    public AuthorizedWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public void addItem(String user, WishlistIdea idea) {
        if(!wishlist.getOwner().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.addItem(new WishlistIdea(idea));
    }

    public boolean containsItem(String user, String idea) {
        return wishlist.containsItem(idea);
    }

    public WishlistIdea getItem(String user, String idea) {
        WishlistIdea foundIdea = new WishlistIdea(wishlist.getItem(idea));

        if(wishlist.getOwner().equals(user)) {
            foundIdea.unclaim();
        }

        return foundIdea;
    }

    public void removeItem(String user, String idea) {
        if(!wishlist.getOwner().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.removeItem(idea);
    }

    public void claimItem(String user, String idea) {
        if(wishlist.getOwner().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.claimItem(user, idea);
    }

    public void unclaimItem(String user, String idea) {
        if(!wishlist.getItem(idea).getClaimer().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.unclaimItem(idea);
    }

    public List<WishlistIdea> rollover(String user) {
        if(!wishlist.getOwner().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        return wishlist.rollover();
    }

    public String getId(String user) {
        return wishlist.getId();
    }

    public List<WishlistIdea> getItems(String user) {
        List<WishlistIdea> ideas = wishlist.getList().stream()
                .map(WishlistIdea::new)
                .collect(Collectors.toList());

        if(wishlist.getOwner().equals(user)) {
            ideas.forEach(WishlistIdea::unclaim);
        }

        return ideas;
    }

    public void updateItem(String user, WishlistIdea idea) {
        if(!wishlist.getOwner().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.updateItem(new WishlistIdea(idea));
    }
}
