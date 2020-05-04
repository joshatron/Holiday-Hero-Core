package io.joshatron.holiday.core;

import org.testng.Assert;
import org.testng.annotations.Test;

public class WishListTest {

    @Test
    public void testAddOne() {
        Person person = new Person();
        GiftIdea idea = new GiftIdea();
        idea.setName("Raspberry Pi");
        person.addToWishList(idea);

        Assert.assertEquals(person.getWishList().size(), 1);
        Assert.assertEquals(person.getWishList().get(0), idea);
    }

    @Test
    public void testAddMultiple() {
        Person person = new Person();
        GiftIdea idea1 = new GiftIdea();
        idea1.setName("Raspberry Pi");
        person.addToWishList(idea1);
        GiftIdea idea2 = new GiftIdea();
        idea2.setName("Arduino");
        person.addToWishList(idea2);

        Assert.assertEquals(person.getWishList().size(), 2);
        Assert.assertTrue(person.getWishList().contains(idea1));
        Assert.assertTrue(person.getWishList().contains(idea2));
    }
}
