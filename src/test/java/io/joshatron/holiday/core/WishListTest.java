package io.joshatron.holiday.core;

import io.joshatron.holiday.core.exception.PersonOperationException;
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
    public void markItemAsClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(1);
        GiftIdea ideaToNotClaim = person1.getMyWishList().get(0);
        person1.claimGiftIdea(person2, ideaToClaim);

        Assert.assertFalse(person1.findTheirItemInWishList(ideaToNotClaim).isClaimed(), "Should not have claimed unclaimed idea.");
        Assert.assertTrue(person1.findTheirItemInWishList(ideaToClaim).isClaimed(), "Should have claimed claimed idea.");
    }

    @Test
    public void checkIfYouClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        Person person3 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(0);
        person1.claimGiftIdea(person3, ideaToClaim);

        Assert.assertNotEquals(person1.findTheirItemInWishList(ideaToClaim).getClaimedBy(), person2, "Should be claimed by person3.");
        Assert.assertEquals(person1.findTheirItemInWishList(ideaToClaim).getClaimedBy(), person3, "Should be claimed by person3.");
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
            Assert.assertEquals(person1.findTheirItemInWishList(ideaToClaim).getClaimedBy(), person2, "Should be claimed by person2 still.");
        } catch (Exception e) {
            Assert.fail("Should have been a person operation exception");
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
