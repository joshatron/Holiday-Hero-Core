package io.joshatron.holiday.core.list.wish;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorizedWishList {
    private WishList wishlist;

    public AuthorizedWishList(WishList wishlist) {
        this.wishlist = wishlist;
    }

    public void addItem(String user, WishListIdea idea) {
        if(!wishlist.getOwner().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.addItem(new WishListIdea(idea));
    }

    public boolean containsItem(String user, String idea) {
        return wishlist.containsItem(idea);
    }

    public WishListIdea getItem(String user, String idea) {
        WishListIdea foundIdea = new WishListIdea(wishlist.getItem(idea));

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

    public List<WishListIdea> rollover(String user) {
        if(!wishlist.getOwner().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        return wishlist.rollover();
    }

    public String getId(String user) {
        return wishlist.getId();
    }

    public List<WishListIdea> getItems(String user) {
        List<WishListIdea> ideas = wishlist.getList().stream()
                .map(WishListIdea::new)
                .collect(Collectors.toList());

        if(wishlist.getOwner().equals(user)) {
            ideas.forEach(WishListIdea::unclaim);
        }

        return ideas;
    }

    public void updateItem(String user, WishListIdea idea) {
        if(!wishlist.getOwner().equals(user)) {
            throw new ListException(ListExceptionReason.USER_NOT_AUTHORIZED);
        }

        wishlist.updateItem(new WishListIdea(idea));
    }
}
