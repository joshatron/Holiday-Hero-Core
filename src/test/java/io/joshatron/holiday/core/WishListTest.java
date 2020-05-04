package io.joshatron.holiday.core;

import org.testng.Assert;
import org.testng.annotations.Test;

public class WishListTest {

    @Test
    public void addOneGiftIdea() {
        Person person = new Person();
        GiftIdea idea = new GiftIdea();
        idea.setName("Raspberry Pi");
        person.addToWishList(idea);

        Assert.assertEquals(person.getMyWishList().size(), 1);
        Assert.assertEquals(person.getMyWishList().get(0), idea);
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

        Assert.assertEquals(person.getMyWishList().size(), 2);
        Assert.assertTrue(person.getMyWishList().contains(idea1));
        Assert.assertTrue(person.getMyWishList().contains(idea2));
    }

    @Test
    public void removeGiftIdeaByObject() {
        Person person = createPersonWith2ItemList();
        GiftIdea ideaToRemove = person.getMyWishList().get(0);

        person.removeFromWishList(ideaToRemove);

        Assert.assertEquals(person.getMyWishList().size(), 1);
        Assert.assertFalse(person.getMyWishList().contains(ideaToRemove));
    }

    @Test
    public void markItemAsClaimed() {
        Person person1 = createPersonWith2ItemList();
        Person person2 = new Person();
        GiftIdea ideaToClaim = person1.getMyWishList().get(1);
        person1.claimGiftIdea(person2, ideaToClaim);

        Assert.assertEquals(person1.getMyWishList().size(), 2);
        Assert.assertFalse(person1.getTheirWishList().get(0).isClaimed());
        Assert.assertTrue(person1.getTheirWishList().get(1).isClaimed());
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
