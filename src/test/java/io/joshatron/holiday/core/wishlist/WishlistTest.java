package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
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

        Assert.assertEquals(wishlist.getList().size(), 1, "Should only have 1 item in it.");
        Assert.assertEquals(wishlist.getList().get(0), idea, "Should contain the item passed.");
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

        Assert.assertEquals(wishlist.getList().size(), 2, "Should contain 2 items in it.");
        Assert.assertTrue(wishlist.containsIdea(idea1.getId()), "One element should be the first item added.");
        Assert.assertTrue(wishlist.containsIdea(idea2.getId()), "One element should be the second item added.");
        Assert.assertFalse(wishlist.containsIdea(randomId()), "One element should be the second item added.");
    }

    @Test
    public void addDuplicateIdea() {
        Wishlist wishlist = new Wishlist(randomId(), randomId());
        WishlistIdea firstIdea = new WishlistIdea(randomId());
        firstIdea.setName("Raspberry Pi");
        WishlistIdea copiedIdea = new WishlistIdea(firstIdea.getId());
        copiedIdea.setName("Arduino");

        wishlist.addIdea(firstIdea);

        try {
            wishlist.addIdea(copiedIdea);
            Assert.fail("Should have thrown an exception.");
        } catch(ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_ALREADY_ADDED, "Should have been proper exception type.");
            Assert.assertEquals(wishlist.getList().size(), 1, "There should only be one item in the list.");
            Assert.assertEquals(wishlist.getIdea(firstIdea.getId()), firstIdea, "The idea should be the first added.");
        } catch(Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void removeGiftIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaToRemove = wishlist.getList().get(0);
        WishlistIdea ideaToKeep = wishlist.getList().get(1);

        wishlist.removeIdea(ideaToRemove.getId());

        Assert.assertEquals(wishlist.getList().size(), 1, "Should only have one item left in it.");
        Assert.assertFalse(wishlist.containsIdea(ideaToRemove.getId()), "Should not contain the removed item.");
        Assert.assertTrue(wishlist.containsIdea(ideaToKeep.getId()), "Should contain the kept item.");
    }

    @Test
    public void removeGiftIdeaTwice() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaToRemove = wishlist.getList().get(1);
        WishlistIdea ideaToKeep = wishlist.getList().get(0);

        wishlist.removeIdea(ideaToRemove.getId());

        try {
            wishlist.removeIdea(ideaToRemove.getId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND);
            Assert.assertEquals(wishlist.getList().size(), 1, "Should only have one item left in it.");
            Assert.assertFalse(wishlist.containsIdea(ideaToRemove.getId()), "Should not contain the removed item.");
            Assert.assertTrue(wishlist.containsIdea(ideaToKeep.getId()), "Should contain the kept item.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void getGiftIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaInList = wishlist.getList().get(1);

        WishlistIdea found = wishlist.getIdea(ideaInList.getId());
        Assert.assertEquals(found, ideaInList, "The found idea and one to find should match.");
    }

    @Test
    public void cantFindGiftIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaNotInList = new WishlistIdea(randomId());

        try {
            wishlist.getIdea(ideaNotInList.getId());
            Assert.fail("Should have thrown exception since not in list.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND,
                    "The reason should be IDEA_NOT_FOUND.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void updateIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaToUpdate = wishlist.getList().get(0);
        WishlistIdea updatedIdea = new WishlistIdea(ideaToUpdate.getId());
        updatedIdea.setName("Bike");

        wishlist.updateIdea(updatedIdea);
        Assert.assertEquals(wishlist.getIdea(ideaToUpdate.getId()).getName(), updatedIdea.getName(), "Name should have changed.");
    }

    @Test
    public void updateNonexistentIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaUpdate = new WishlistIdea(randomId());

        try {
            wishlist.updateIdea(ideaUpdate);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND);
            Assert.assertFalse(wishlist.containsIdea(ideaUpdate.getId()));
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void updateDoesNotChangeClaim() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea ideaClaimed = wishlist.getList().get(0);
        WishlistIdea ideaNotClaimed = wishlist.getList().get(1);
        String claimer = randomId();
        wishlist.claimIdea(claimer, ideaClaimed.getId());

        WishlistIdea updateClaimed = new WishlistIdea(ideaClaimed.getId());
        updateClaimed.setName("3D Printer");

        WishlistIdea updateUnclaimed = new WishlistIdea(ideaNotClaimed.getId());
        updateUnclaimed.setName("3D Printing Filament");
        updateUnclaimed.setClaimer(randomId());

        wishlist.updateIdea(updateClaimed);
        wishlist.updateIdea(updateUnclaimed);

        Assert.assertTrue(wishlist.getIdea(ideaClaimed.getId()).isClaimed());
        Assert.assertEquals(wishlist.getIdea(ideaClaimed.getId()).getClaimer(), claimer);

        Assert.assertFalse(wishlist.getIdea(ideaNotClaimed.getId()).isClaimed());
    }

    @Test
    public void markIdeaAsClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaToClaim = wishlist.getList().get(1);
        WishlistIdea ideaToNotClaim = wishlist.getList().get(0);
        wishlist.claimIdea(claimer, ideaToClaim.getId());

        Assert.assertFalse(wishlist.getIdea(ideaToNotClaim.getId()).isClaimed(),
                "Should not have claimed unclaimed idea.");
        Assert.assertTrue(wishlist.getIdea(ideaToClaim.getId()).isClaimed(),
                "Should have claimed claimed idea.");
    }

    @Test
    public void claimIdeaThatDoesNotExist() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        String invalidIdea = randomId();

        try {
            wishlist.claimIdea(claimer, invalidIdea);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be idea not found.");
            Assert.assertFalse(wishlist.containsIdea(invalidIdea));
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void checkClaimer() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        String ideaToClaim = wishlist.getList().get(0).getId();
        wishlist.claimIdea(claimer, ideaToClaim);

        Assert.assertEquals(wishlist.getIdea(ideaToClaim).getClaimer(), claimer,
                "Should be claimed by person3.");
    }

    @Test
    public void tryToClaimAlreadyClaimed() {
        Wishlist wishlist = create2ItemWishlist();
        String firstClaimer = randomId();
        String secondClaimer = randomId();
        WishlistIdea ideaToClaim = wishlist.getList().get(1);
        wishlist.claimIdea(firstClaimer, ideaToClaim.getId());

        try {
            wishlist.claimIdea(secondClaimer, ideaToClaim.getId());
            Assert.fail("Should have thrown exception because reclaiming.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ALREADY_CLAIMED,
                    "The reason should have been ALREADY_CLAIMED.");
            Assert.assertEquals(wishlist.getIdea(ideaToClaim.getId()).getClaimer(), firstClaimer,
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
            wishlist.claimIdea(wishlist.getOwner(), ideaToClaim.getId());
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertFalse(wishlist.getIdea(ideaToClaim.getId()).isClaimed(),
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
        wishlist.claimIdea(claimer, ideaToClaim.getId());

        try {
            wishlist.claimIdea(wishlist.getOwner(), ideaToClaim.getId());
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertEquals(wishlist.getIdea(ideaToClaim.getId()).getClaimer(), claimer,
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
        wishlist.claimIdea(claimer, ideaClaimed.getId());

        try {
            wishlist.removeIdea(ideaClaimed.getId());
            Assert.fail("Should have thrown exception removing item.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.CLAIMING_STARTED, "Should be the proper reason.");
            Assert.assertTrue(wishlist.containsIdea(ideaClaimed.getId()), "Should not have removed the idea.");
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
        wishlist.claimIdea(claimer, ideaClaimed.getId());

        try {
            wishlist.removeIdea(ideaNotClaimed.getId());
            Assert.fail("Should have thrown exception removing item.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.CLAIMING_STARTED);
            Assert.assertTrue(wishlist.containsIdea(ideaNotClaimed.getId()));
        } catch (Exception e) {
            Assert.fail("Should only have thrown PersonOperationException.");
        }
    }

    @Test
    public void addItemAfterClaimingStarted() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        wishlist.claimIdea(claimer, wishlist.getList().get(0).getId());

        WishlistIdea newIdea = new WishlistIdea(randomId());
        newIdea.setName("Sonic Screwdriver");
        wishlist.addIdea(newIdea);

        Assert.assertTrue(wishlist.containsIdea(newIdea.getId()));
    }

    @Test
    public void unclaimIdea() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getList().get(1);
        wishlist.claimIdea(claimer, ideaClaimed.getId());
        wishlist.unclaimIdea(ideaClaimed.getId());

        Assert.assertFalse(wishlist.getIdea(ideaClaimed.getId()).isClaimed(), "Should not be claimed anymore.");
    }

    @Test
    public void unclaimUnclaimedIdea() {
        Wishlist wishlist = create2ItemWishlist();
        WishlistIdea idea = wishlist.getList().get(0);

        try {
            wishlist.unclaimIdea(idea.getId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_CLAIMED, "Reason should be idea not claimed.");
            Assert.assertFalse(wishlist.getIdea(idea.getId()).isClaimed(), "Should still not be claimed.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void unclaimNonpresentIdea() {
        Wishlist wishlist = create2ItemWishlist();
        String idea = randomId();

        try {
            wishlist.unclaimIdea(idea);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be idea not claimed.");
            Assert.assertFalse(wishlist.containsIdea(idea));
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
        wishlist.claimIdea(firstClaimer, ideaClaimed.getId());
        wishlist.unclaimIdea(ideaClaimed.getId());
        wishlist.claimIdea(secondClaimer, ideaClaimed.getId());

        Assert.assertEquals(wishlist.getIdea(ideaClaimed.getId()).getClaimer(), secondClaimer,
                "Should be claimed again by second claimer.");
    }

    @Test
    public void rolloverWishlist() {
        Wishlist wishlist = create2ItemWishlist();
        String claimer = randomId();
        WishlistIdea ideaClaimed = wishlist.getList().get(0);
        WishlistIdea ideaNotClaimed = wishlist.getList().get(1);
        wishlist.claimIdea(claimer, ideaClaimed.getId());
        List<WishlistIdea> rolledOver = wishlist.rollover();

        Assert.assertFalse(wishlist.containsIdea(ideaClaimed.getId()), "Claimed item should no longer be in wishlist.");
        Assert.assertTrue(wishlist.containsIdea(ideaNotClaimed.getId()), "Unclaimed item should stay in wishlist.");

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
