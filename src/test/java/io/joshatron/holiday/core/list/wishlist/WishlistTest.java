package io.joshatron.holiday.core.list.wishlist;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

public class WishlistTest {
    @Test
    public void updateDoesNotChangeClaim() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaClaimed = wishlist.getList().get(0);
        WishlistIdea ideaNotClaimed = wishlist.getList().get(1);
        String claimer = randomId();
        wishlist.claimItem(claimer, ideaClaimed.getId());

        WishlistIdea updateClaimed = new WishlistIdea(ideaClaimed.getId());
        updateClaimed.setName("3D Printer");

        WishlistIdea updateUnclaimed = new WishlistIdea(ideaNotClaimed.getId());
        updateUnclaimed.setName("3D Printing Filament");
        updateUnclaimed.setClaimer(randomId());

        wishlist.updateItem(updateClaimed);
        wishlist.updateItem(updateUnclaimed);

        Assert.assertTrue(wishlist.getItem(ideaClaimed.getId()).isClaimed());
        Assert.assertEquals(wishlist.getItem(ideaClaimed.getId()).getClaimer(), claimer);

        Assert.assertFalse(wishlist.getItem(ideaNotClaimed.getId()).isClaimed());
    }

    @Test
    public void markIdeaAsClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaToClaim = wishlist.getList().get(1);
        WishlistIdea ideaToNotClaim = wishlist.getList().get(0);
        wishlist.claimItem(claimer, ideaToClaim.getId());

        Assert.assertFalse(wishlist.getItem(ideaToNotClaim.getId()).isClaimed(),
                "Should not have claimed unclaimed idea.");
        Assert.assertTrue(wishlist.getItem(ideaToClaim.getId()).isClaimed(),
                "Should have claimed claimed idea.");
    }

    @Test
    public void claimIdeaThatDoesNotExist() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        String invalidIdea = randomId();

        try {
            wishlist.claimItem(claimer, invalidIdea);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be idea not found.");
            Assert.assertFalse(wishlist.containsItem(invalidIdea));
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void checkClaimer() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        String ideaToClaim = wishlist.getList().get(0).getId();
        wishlist.claimItem(claimer, ideaToClaim);

        Assert.assertEquals(wishlist.getItem(ideaToClaim).getClaimer(), claimer,
                "Should be claimed by person3.");
    }

    @Test
    public void tryToClaimAlreadyClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String firstClaimer = randomId();
        String secondClaimer = randomId();
        WishlistIdea ideaToClaim = wishlist.getList().get(1);
        wishlist.claimItem(firstClaimer, ideaToClaim.getId());

        try {
            wishlist.claimItem(secondClaimer, ideaToClaim.getId());
            Assert.fail("Should have thrown exception because reclaiming.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ALREADY_CLAIMED,
                    "The reason should have been ALREADY_CLAIMED.");
            Assert.assertEquals(wishlist.getItem(ideaToClaim.getId()).getClaimer(), firstClaimer,
                    "Should be claimed by firstClaimer still.");
        } catch (Exception e) {
            Assert.fail("Should have been a list exception");
        }
    }

    @Test
    public void tryToClaimOwn() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaToClaim = wishlist.getList().get(1);

        try {
            wishlist.claimItem(wishlist.getOwner(), ideaToClaim.getId());
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertFalse(wishlist.getItem(ideaToClaim.getId()).isClaimed(),
                    "The idea should not have been claimed.");
        } catch (Exception e) {
            Assert.fail("Should have been a list exception");
        }
    }

    @Test
    public void tryToClaimOwnAlreadyClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaToClaim = wishlist.getList().get(0);
        wishlist.claimItem(claimer, ideaToClaim.getId());

        try {
            wishlist.claimItem(wishlist.getOwner(), ideaToClaim.getId());
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertEquals(wishlist.getItem(ideaToClaim.getId()).getClaimer(), claimer,
                    "Should still be claimed by claimer.");
        } catch (Exception e) {
            Assert.fail("Should have been a list exception");
        }
    }

    @Test
    public void tryToRemoveItemAfterClaiming() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getList().get(0);
        wishlist.claimItem(claimer, ideaClaimed.getId());

        try {
            wishlist.removeItem(ideaClaimed.getId());
            Assert.fail("Should have thrown exception removing item.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.CLAIMING_STARTED, "Should be the proper reason.");
            Assert.assertTrue(wishlist.containsItem(ideaClaimed.getId()), "Should not have removed the idea.");
        } catch (Exception e) {
            Assert.fail("Should only have thrown list exception.");
        }
    }

    @Test
    public void tryToRemoveOtherItemAfterClaiming() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getList().get(0);
        WishlistIdea ideaNotClaimed = wishlist.getList().get(1);
        wishlist.claimItem(claimer, ideaClaimed.getId());

        try {
            wishlist.removeItem(ideaNotClaimed.getId());
            Assert.fail("Should have thrown exception removing item.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.CLAIMING_STARTED);
            Assert.assertTrue(wishlist.containsItem(ideaNotClaimed.getId()));
        } catch (Exception e) {
            Assert.fail("Should only have thrown PersonOperationException.");
        }
    }

    @Test
    public void unclaimItem() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getList().get(1);
        wishlist.claimItem(claimer, ideaClaimed.getId());
        wishlist.unclaimItem(ideaClaimed.getId());

        Assert.assertFalse(wishlist.getItem(ideaClaimed.getId()).isClaimed(), "Should not be claimed anymore.");
    }

    @Test
    public void unclaimUnclaimedIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea idea = wishlist.getList().get(0);

        try {
            wishlist.unclaimItem(idea.getId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_CLAIMED, "Reason should be idea not claimed.");
            Assert.assertFalse(wishlist.getItem(idea.getId()).isClaimed(), "Should still not be claimed.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void unclaimNonpresentIdea() {
        Wishlist wishlist = create2ItemWishlist();
        String idea = randomId();

        try {
            wishlist.unclaimItem(idea);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be idea not claimed.");
            Assert.assertFalse(wishlist.containsItem(idea));
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void claimAfterUnclaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String firstClaimer = randomId();
        String secondClaimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getList().get(1);
        wishlist.claimItem(firstClaimer, ideaClaimed.getId());
        wishlist.unclaimItem(ideaClaimed.getId());
        wishlist.claimItem(secondClaimer, ideaClaimed.getId());

        Assert.assertEquals(wishlist.getItem(ideaClaimed.getId()).getClaimer(), secondClaimer,
                "Should be claimed again by second claimer.");
    }

    @Test
    public void rolloverWishlist() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getList().get(0);
        WishlistIdea ideaNotClaimed = wishlist.getList().get(1);
        wishlist.claimItem(claimer, ideaClaimed.getId());
        List<WishlistIdea> rolledOver = wishlist.rollover();

        Assert.assertFalse(wishlist.containsItem(ideaClaimed.getId()), "Claimed item should no longer be in wishlist.");
        Assert.assertTrue(wishlist.containsItem(ideaNotClaimed.getId()), "Unclaimed item should stay in wishlist.");

        Assert.assertTrue(rolledOver.contains(ideaClaimed), "Claimed item should now be in proposed list.");
        Assert.assertFalse(rolledOver.contains(ideaNotClaimed), "Unclaimed item should not be in proposed list.");
    }

    @Test
    public void rolloverWhenNoneClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        List<WishlistIdea> rolledOver = wishlist.rollover();

        Assert.assertTrue(rolledOver.isEmpty());
        Assert.assertEquals(wishlist.getList().size(), 2);
    }

    private Wishlist create2ItemWishlist() {
        String person = randomId();
        Wishlist wishlist = new Wishlist(randomId(), person);
        WishlistIdea idea1 = new WishlistIdea(randomId());
        idea1.setName("Raspberry Pi");
        wishlist.addItem(idea1);
        WishlistIdea idea2 = new WishlistIdea(randomId());
        idea2.setName("Arduino");
        wishlist.addItem(idea2);

        return wishlist;
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
