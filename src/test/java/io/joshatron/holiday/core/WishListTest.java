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
        person.addToWishList(idea);

        Assert.assertEquals(person.getMyWishList().size(), 1, "Should only have 1 item in it.");
        Assert.assertEquals(person.getMyWishList().get(0), idea, "Should contain the item passed.");
    }

    @Test
    public void addMultipleGiftIdeas() {
        Person person = new Person();
        GiftIdea idea1 = new GiftIdea();
        idea1.setName("Raspberry Pi");
        person.addToWishList(idea1);
        GiftIdea idea2 = new GiftIdea();
        idea2.setName("Arduino");
        person.addToWishList(idea2);

        Assert.assertEquals(person.getMyWishList().size(), 2, "Should contain 2 items in it.");
        Assert.assertTrue(person.getMyWishList().contains(idea1), "One element should be the first item added.");
        Assert.assertTrue(person.getMyWishList().contains(idea2), "One element should be the second item added.");
    }

    @Test
    public void removeGiftIdeaByObject() {
        Person person = createPersonWith2ItemList();
        GiftIdea ideaToRemove = person.getMyWishList().get(0);
        GiftIdea ideaToKeep = person.getMyWishList().get(1);

        person.removeFromWishList(ideaToRemove);

        Assert.assertEquals(person.getMyWishList().size(), 1, "Should only have one item left in it.");
        Assert.assertFalse(person.getMyWishList().contains(ideaToRemove), "Should not contain the removed item.");
        Assert.assertTrue(person.getMyWishList().contains(ideaToKeep), "Should contain the kept item.");
    }

    @Test
    public void findGiftIdeaByObject() {
        Person person = createPersonWith2ItemList();
        GiftIdea ideaInList = person.getMyWishList().get(1);

        GiftIdeaAndStatus found = person.findTheirItemInWishList(ideaInList);
        Assert.assertEquals(found.getIdea(), ideaInList, "The found idea and one to find should match.");
    }

    @Test
    public void cantFindGiftIdeaByObject() {
        Person person = createPersonWith2ItemList();
        GiftIdea ideaNotInList = new GiftIdea();

        try {
            person.findTheirItemInWishList(ideaNotInList);
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
        person1.claimGiftIdea(person2, ideaToClaim);

        Assert.assertFalse(person1.findTheirItemInWishList(ideaToNotClaim).isClaimed(),
                "Should not have claimed unclaimed idea.");
        Assert.assertTrue(person1.findTheirItemInWishList(ideaToClaim).isClaimed(),
                "Should have claimed claimed idea.");
    }

    @Test
    public void checkIfYouClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        Person person3 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(0);
        person1.claimGiftIdea(person3, ideaToClaim);

        Assert.assertNotEquals(person1.findTheirItemInWishList(ideaToClaim).getClaimedBy(), person2,
                "Should be claimed by person3.");
        Assert.assertEquals(person1.findTheirItemInWishList(ideaToClaim).getClaimedBy(), person3,
                "Should be claimed by person3.");
    }

    @Test
    public void tryToClaimAlreadyClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        Person person3 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(1);
        person1.claimGiftIdea(person2, ideaToClaim);

        try {
            person1.claimGiftIdea(person3, ideaToClaim);
            Assert.fail("Should have thrown exception because reclaiming.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.ALREADY_CLAIMED,
                    "The reason should have been ALREADY_CLAIMED.");
            Assert.assertEquals(person1.findTheirItemInWishList(ideaToClaim).getClaimedBy(), person2,
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
            person.claimGiftIdea(person, ideaToClaim);
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertFalse(person.findTheirItemInWishList(ideaToClaim).isClaimed(),
                    "The idea should not have been claimed.");
        }
    }

    @Test
    public void tryToClaimOwnAlreadyClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(0);
        person1.claimGiftIdea(person2, ideaToClaim);

        try {
            person1.claimGiftIdea(person1, ideaToClaim);
            Assert.fail("Should have thrown an exception trying to claim.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.CANT_CLAIM_OWN,
                    "The reason should have been CANT_CLAIM_OWN.");
            Assert.assertEquals(person1.findTheirItemInWishList(ideaToClaim).getClaimedBy(), person2,
                    "Should still be claimed by person2.");
        }
    }

    @Test
    public void tryToRemoveItemAfterClaiming() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaClaimed = person1.getMyWishList().get(0);
        person1.claimGiftIdea(person2, ideaClaimed);

        try {
            person1.removeFromWishList(ideaClaimed);
            Assert.fail("Should have thrown exception removing item.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.CLAIMING_STARTED);
            try {
                person1.findTheirItemInWishList(ideaClaimed);
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
        person1.claimGiftIdea(person2, ideaClaimed);

        try {
            person1.removeFromWishList(ideaNotClaimed);
            Assert.fail("Should have thrown exception removing item.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.CLAIMING_STARTED);
            try {
                person1.findTheirItemInWishList(ideaNotClaimed);
            } catch (PersonOperationException e2) {
                Assert.fail("Should not have removed the item.");
            }
        } catch (Exception e) {
            Assert.fail("Should only have thrown PersonOperationException.");
        }
    }

    private Person createPersonWith2ItemList() {
        Person person = new Person();
        GiftIdea idea1 = new GiftIdea();
        idea1.setName("Raspberry Pi");
        person.addToWishList(idea1);
        GiftIdea idea2 = new GiftIdea();
        idea2.setName("Arduino");
        person.addToWishList(idea2);

        return person;
    }
}
