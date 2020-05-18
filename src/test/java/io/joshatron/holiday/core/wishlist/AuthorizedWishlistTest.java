package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class AuthorizedWishlistTest {
    @Test
    public void onlyOwnerCanAddIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea firstIdea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, firstIdea);
        Assert.assertTrue(authorizedList.containsIdea(owner, firstIdea.getId()), "Should have added idea.");

        WishlistIdea secondIdea = new WishlistIdea(randomId());
        try {
            authorizedList.addIdea(randomId(), secondIdea);
            Assert.fail("Should have thrown exception.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.USER_NOT_AUTHORIZED);
            Assert.assertFalse(authorizedList.containsIdea(owner, secondIdea.getId()));
        } catch (Exception e) {
            Assert.fail("Should have thrown a wishlist exception.");
        }
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
