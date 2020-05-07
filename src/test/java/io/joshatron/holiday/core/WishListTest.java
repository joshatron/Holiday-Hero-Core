package io.joshatron.holiday.core;

import io.joshatron.holiday.core.exception.PersonOperationException;
import io.joshatron.holiday.core.exception.PersonOperationExceptionReason;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WishListTest {

    @Test
    public void addOneGiftIdea() {
        Person person = new Person();
        GiftIdea idea = new GiftIdea();
        idea.setName("Raspberry Pi");
        person.addIdeaToWishList(idea);

        Assert.assertEquals(person.getMyWishList().size(), 1, "Should only have 1 item in it.");
        Assert.assertEquals(person.getMyWishList().get(0), idea, "Should contain the item passed.");
    }

    @Test
    public void addMultipleGiftIdeas() {
        Person person = new Person();
        GiftIdea idea1 = new GiftIdea();
        idea1.setName("Raspberry Pi");
        person.addIdeaToWishList(idea1);
        GiftIdea idea2 = new GiftIdea();
        idea2.setName("Arduino");
        person.addIdeaToWishList(idea2);

        Assert.assertEquals(person.getMyWishList().size(), 2, "Should contain 2 items in it.");
        Assert.assertTrue(person.wishlistContainsIdea(idea1), "One element should be the first item added.");
        Assert.assertTrue(person.wishlistContainsIdea(idea2), "One element should be the second item added.");
    }

    @Test
    public void removeGiftIdeaByObject() {
        Person person = createPersonWith2ItemList();
        GiftIdea ideaToRemove = person.getMyWishList().get(0);
        GiftIdea ideaToKeep = person.getMyWishList().get(1);

        person.removeIdeaFromWishList(ideaToRemove);

        Assert.assertEquals(person.getMyWishList().size(), 1, "Should only have one item left in it.");
        Assert.assertFalse(person.wishlistContainsIdea(ideaToRemove), "Should not contain the removed item.");
        Assert.assertTrue(person.wishlistContainsIdea(ideaToKeep), "Should contain the kept item.");
    }

    @Test
    public void findGiftIdeaByObject() {
        Person person = createPersonWith2ItemList();
        GiftIdea ideaInList = person.getMyWishList().get(1);

        GiftIdeaAndStatus found = person.findTheirItemInWishlist(ideaInList);
        Assert.assertEquals(found.getIdea(), ideaInList, "The found idea and one to find should match.");
    }

    @Test
    public void cantFindGiftIdeaByObject() {
        Person person = createPersonWith2ItemList();
        GiftIdea ideaNotInList = new GiftIdea();

        try {
            person.findTheirItemInWishlist(ideaNotInList);
            Assert.fail("Should have thrown exception since not in list.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.IDEA_NOT_FOUND,
                    "The reason should be IDEA_NOT_FOUND.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a PersonOperationException.");
        }
    }

    @Test
    public void markItemAsClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(1);
        GiftIdea ideaToNotClaim = person1.getMyWishList().get(0);
        person1.claimGiftIdeaInWishlist(person2, ideaToClaim);

        Assert.assertFalse(person1.findTheirItemInWishlist(ideaToNotClaim).isClaimed(),
                "Should not have claimed unclaimed idea.");
        Assert.assertTrue(person1.findTheirItemInWishlist(ideaToClaim).isClaimed(),
                "Should have claimed claimed idea.");
    }

    @Test
    public void checkIfYouClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        Person person3 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(0);
        person1.claimGiftIdeaInWishlist(person3, ideaToClaim);

        Assert.assertNotEquals(person1.findTheirItemInWishlist(ideaToClaim).getClaimedBy(), person2,
                "Should be claimed by person3.");
        Assert.assertEquals(person1.findTheirItemInWishlist(ideaToClaim).getClaimedBy(), person3,
                "Should be claimed by person3.");
    }

    @Test
    public void tryToClaimAlreadyClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        Person person3 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(1);
        person1.claimGiftIdeaInWishlist(person2, ideaToClaim);

        try {
            person1.claimGiftIdeaInWishlist(person3, ideaToClaim);
            Assert.fail("Should have thrown exception because reclaiming.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.ALREADY_CLAIMED,
                    "The reason should have been ALREADY_CLAIMED.");
            Assert.assertEquals(person1.findTheirItemInWishlist(ideaToClaim).getClaimedBy(), person2,
                    "Should be claimed by person2 still.");
        } catch (Exception e) {
            Assert.fail("Should have been a person operation exception");
        }
    }

    @Test
    public void tryToClaimOwn() {
        Person person = createPersonWith2ItemList();
        GiftIdea ideaToClaim = person.getMyWishList().get(1);

        try {
            person.claimGiftIdeaInWishlist(person, ideaToClaim);
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertFalse(person.findTheirItemInWishlist(ideaToClaim).isClaimed(),
                    "The idea should not have been claimed.");
        }
    }

    @Test
    public void tryToClaimOwnAlreadyClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(0);
        person1.claimGiftIdeaInWishlist(person2, ideaToClaim);

        try {
            person1.claimGiftIdeaInWishlist(person1, ideaToClaim);
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertEquals(person1.findTheirItemInWishlist(ideaToClaim).getClaimedBy(), person2,
                    "Should still be claimed by person2.");
        }
    }

    @Test
    public void tryToRemoveItemAfterClaiming() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaClaimed = person1.getMyWishList().get(0);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);

        try {
            person1.removeIdeaFromWishList(ideaClaimed);
            Assert.fail("Should have thrown exception removing item.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.CLAIMING_STARTED);
            try {
                person1.findTheirItemInWishlist(ideaClaimed);
            } catch (PersonOperationException e2) {
                Assert.fail("Should not have removed the item.");
            }
        } catch (Exception e) {
            Assert.fail("Should only have thrown PersonOperationException.");
        }
    }

    @Test
    public void tryToRemoveOtherItemAfterClaiming() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaClaimed = person1.getMyWishList().get(0);
        GiftIdea ideaNotClaimed = person1.getMyWishList().get(1);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);

        try {
            person1.removeIdeaFromWishList(ideaNotClaimed);
            Assert.fail("Should have thrown exception removing item.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.CLAIMING_STARTED);
            try {
                person1.findTheirItemInWishlist(ideaNotClaimed);
            } catch (PersonOperationException e2) {
                Assert.fail("Should not have removed the item.");
            }
        } catch (Exception e) {
            Assert.fail("Should only have thrown PersonOperationException.");
        }
    }

    @Test
    public void addItemAfterClaimingStarted() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        person1.claimGiftIdeaInWishlist(person2, person1.getMyWishList().get(0));

        GiftIdea newIdea = new GiftIdea();
        newIdea.setName("Sonic Screwdriver");
        person1.addIdeaToWishList(newIdea);

        Assert.assertTrue(person1.wishlistContainsIdea(newIdea));
    }

    @Test
    public void unclaimIdea() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaClaimed = person1.getMyWishList().get(1);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);
        person1.removeClaimFromWishlist(ideaClaimed);

        Assert.assertFalse(person1.findTheirItemInWishlist(ideaClaimed).isClaimed(), "Should not be claimed anymore.");
    }

    @Test
    public void claimAfterUnclaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaClaimed = person1.getMyWishList().get(1);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);
        person1.removeClaimFromWishlist(ideaClaimed);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);

        Assert.assertEquals(person1.findTheirItemInWishlist(ideaClaimed).getClaimedBy(), person2,
                "Should be claimed again by person2.");
    }

    @Test
    public void rolloverWishlist() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaClaimed = person1.getMyWishList().get(0);
        GiftIdea ideaNotClaimed = person1.getMyWishList().get(1);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);
        person1.rolloverWishlist();

        Assert.assertFalse(person1.wishlistContainsIdea(ideaClaimed), "Claimed item should no longer be in wishlist.");
        Assert.assertTrue(person1.wishlistContainsIdea(ideaNotClaimed), "Unclaimed item should stay in wishlist.");

        Assert.assertTrue(person1.proposedListContainsIdea(ideaClaimed), "Claimed item should now be in proposed list.");
        Assert.assertFalse(person1.proposedListContainsIdea(ideaNotClaimed), "Unclaimed item should not be in proposed list.");
    }

    private Person createPersonWith2ItemList() {
        Person person = new Person();
        GiftIdea idea1 = new GiftIdea();
        idea1.setName("Raspberry Pi");
        person.addIdeaToWishList(idea1);
        GiftIdea idea2 = new GiftIdea();
        idea2.setName("Arduino");
        person.addIdeaToWishList(idea2);

        return person;
    }
}
