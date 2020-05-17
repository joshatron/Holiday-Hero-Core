package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

public class WishlistTest {

    @Test
    public void addOneGiftIdea() {
        String person = randomId();
        Wishlist wishlist = new Wishlist(randomId(), person);
        WishlistIdea idea = new WishlistIdea(randomId());
        idea.setName("Raspberry Pi");
        wishlist.addIdea(idea);

        Assert.assertEquals(wishlist.getWishlist().size(), 1, "Should only have 1 item in it.");
        Assert.assertEquals(wishlist.getWishlist().get(0), idea, "Should contain the item passed.");
    }

    @Test
    public void addMultipleGiftIdeas() {
        String person = randomId();
        Wishlist wishlist = new Wishlist(randomId(), person);
        WishlistIdea idea1 = new WishlistIdea(randomId());
        idea1.setName("Raspberry Pi");
        wishlist.addIdea(idea1);
        WishlistIdea idea2 = new WishlistIdea(randomId());
        idea2.setName("Arduino");
        wishlist.addIdea(idea2);

        Assert.assertEquals(wishlist.getWishlist().size(), 2, "Should contain 2 items in it.");
        Assert.assertTrue(wishlist.containsIdea(idea1.getId()), "One element should be the first item added.");
        Assert.assertTrue(wishlist.containsIdea(idea2.getId()), "One element should be the second item added.");
        Assert.assertFalse(wishlist.containsIdea(randomId()), "One element should be the second item added.");
    }

    @Test
    public void removeGiftIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaToRemove = wishlist.getWishlist().get(0);
        WishlistIdea ideaToKeep = wishlist.getWishlist().get(1);

        wishlist.removeIdea(ideaToRemove.getId());

        Assert.assertEquals(wishlist.getWishlist().size(), 1, "Should only have one item left in it.");
        Assert.assertFalse(wishlist.containsIdea(ideaToRemove.getId()), "Should not contain the removed item.");
        Assert.assertTrue(wishlist.containsIdea(ideaToKeep.getId()), "Should contain the kept item.");
    }

    @Test
    public void findGiftIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaInList = wishlist.getWishlist().get(1);

        WishlistIdea found = wishlist.findIdea(ideaInList.getId());
        Assert.assertEquals(found, ideaInList, "The found idea and one to find should match.");
    }

    @Test
    public void cantFindGiftIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaNotInList = new WishlistIdea(randomId());

        try {
            wishlist.findIdea(ideaNotInList.getId());
            Assert.fail("Should have thrown exception since not in list.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.IDEA_NOT_FOUND,
                    "The reason should be IDEA_NOT_FOUND.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a wishlist exception.");
        }
    }

    @Test
    public void markItemAsClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaToClaim = wishlist.getWishlist().get(1);
        WishlistIdea ideaToNotClaim = wishlist.getWishlist().get(0);
        wishlist.claimIdea(claimer, ideaToClaim.getId());

        Assert.assertFalse(wishlist.findIdea(ideaToNotClaim.getId()).isClaimed(),
                "Should not have claimed unclaimed idea.");
        Assert.assertTrue(wishlist.findIdea(ideaToClaim.getId()).isClaimed(),
                "Should have claimed claimed idea.");
    }

    @Test
    public void checkClaimer() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        String ideaToClaim = wishlist.getWishlist().get(0).getId();
        wishlist.claimIdea(claimer, ideaToClaim);

        Assert.assertEquals(wishlist.findIdea(ideaToClaim).getClaimer(), claimer,
                "Should be claimed by person3.");
    }

    @Test
    public void tryToClaimAlreadyClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String firstClaimer = randomId();
        String secondClaimer = randomId();
        WishlistIdea ideaToClaim = wishlist.getWishlist().get(1);
        wishlist.claimIdea(firstClaimer, ideaToClaim.getId());

        try {
            wishlist.claimIdea(secondClaimer, ideaToClaim.getId());
            Assert.fail("Should have thrown exception because reclaiming.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.ALREADY_CLAIMED,
                    "The reason should have been ALREADY_CLAIMED.");
            Assert.assertEquals(wishlist.findIdea(ideaToClaim.getId()).getClaimer(), firstClaimer,
                    "Should be claimed by firstClaimer still.");
        } catch (Exception e) {
            Assert.fail("Should have been a wishlist exception");
        }
    }

    @Test
    public void tryToClaimOwn() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaToClaim = wishlist.getWishlist().get(1);

        try {
            wishlist.claimIdea(wishlist.getOwner(), ideaToClaim.getId());
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertFalse(wishlist.findIdea(ideaToClaim.getId()).isClaimed(),
                    "The idea should not have been claimed.");
        } catch (Exception e) {
            Assert.fail("Should have been a wishlist exception");
        }
    }

    @Test
    public void tryToClaimOwnAlreadyClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaToClaim = wishlist.getWishlist().get(0);
        wishlist.claimIdea(claimer, ideaToClaim.getId());

        try {
            wishlist.claimIdea(wishlist.getOwner(), ideaToClaim.getId());
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertEquals(wishlist.findIdea(ideaToClaim.getId()).getClaimer(), claimer,
                    "Should still be claimed by claimer.");
        } catch (Exception e) {
            Assert.fail("Should have been a wishlist exception");
        }
    }

    @Test
    public void tryToRemoveItemAfterClaiming() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getWishlist().get(0);
        wishlist.claimIdea(claimer, ideaClaimed.getId());

        try {
            wishlist.removeIdea(ideaClaimed.getId());
            Assert.fail("Should have thrown exception removing item.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.CLAIMING_STARTED, "Should be the proper reason.");
            Assert.assertTrue(wishlist.containsIdea(ideaClaimed.getId()), "Should not have removed the idea.");
        } catch (Exception e) {
            Assert.fail("Should only have thrown wishlist exception.");
        }
    }

    @Test
    public void tryToRemoveOtherItemAfterClaiming() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getWishlist().get(0);
        WishlistIdea ideaNotClaimed = wishlist.getWishlist().get(1);
        wishlist.claimIdea(claimer, ideaClaimed.getId());

        try {
            wishlist.removeIdea(ideaNotClaimed.getId());
            Assert.fail("Should have thrown exception removing item.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.CLAIMING_STARTED);
            Assert.assertTrue(wishlist.containsIdea(ideaNotClaimed.getId()));
        } catch (Exception e) {
            Assert.fail("Should only have thrown PersonOperationException.");
        }
    }

    @Test
    public void addItemAfterClaimingStarted() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        wishlist.claimIdea(claimer, wishlist.getWishlist().get(0).getId());

        WishlistIdea newIdea = new WishlistIdea(randomId());
        newIdea.setName("Sonic Screwdriver");
        wishlist.addIdea(newIdea);

        Assert.assertTrue(wishlist.containsIdea(newIdea.getId()));
    }

    @Test
    public void unclaimIdea() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getWishlist().get(1);
        wishlist.claimIdea(claimer, ideaClaimed.getId());
        wishlist.unclaimIdea(ideaClaimed.getId());

        Assert.assertFalse(wishlist.findIdea(ideaClaimed.getId()).isClaimed(), "Should not be claimed anymore.");
    }

    @Test
    public void claimAfterUnclaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String firstClaimer = randomId();
        String secondClaimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getWishlist().get(1);
        wishlist.claimIdea(firstClaimer, ideaClaimed.getId());
        wishlist.unclaimIdea(ideaClaimed.getId());
        wishlist.claimIdea(secondClaimer, ideaClaimed.getId());

        Assert.assertEquals(wishlist.findIdea(ideaClaimed.getId()).getClaimer(), secondClaimer,
                "Should be claimed again by second claimer.");
    }

    @Test
    public void rolloverWishlist() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getWishlist().get(0);
        WishlistIdea ideaNotClaimed = wishlist.getWishlist().get(1);
        wishlist.claimIdea(claimer, ideaClaimed.getId());
        List<WishlistIdea> rolledOver = wishlist.rollover();

        Assert.assertFalse(wishlist.containsIdea(ideaClaimed.getId()), "Claimed item should no longer be in wishlist.");
        Assert.assertTrue(wishlist.containsIdea(ideaNotClaimed.getId()), "Unclaimed item should stay in wishlist.");

        Assert.assertTrue(rolledOver.contains(ideaClaimed), "Claimed item should now be in proposed list.");
        Assert.assertFalse(rolledOver.contains(ideaNotClaimed), "Unclaimed item should not be in proposed list.");
    }

    private Wishlist create2ItemWishlist() {
        String person = randomId();
        Wishlist wishlist = new Wishlist(randomId(), person);
        WishlistIdea idea1 = new WishlistIdea(randomId());
        idea1.setName("Raspberry Pi");
        wishlist.addIdea(idea1);
        WishlistIdea idea2 = new WishlistIdea(randomId());
        idea2.setName("Arduino");
        wishlist.addIdea(idea2);

        return wishlist;
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
