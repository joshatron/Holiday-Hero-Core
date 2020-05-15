package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.WishlistIdea;
import io.joshatron.holiday.core.Person;
import io.joshatron.holiday.core.exception.PersonOperationException;
import io.joshatron.holiday.core.exception.PersonOperationExceptionReason;
import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;
import org.testng.Assert;
import org.testng.annotations.Test;

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
        Person person1 = createPersonWith2IdeaWishlist();
        Person person2 = new Person();
        WishlistIdea ideaClaimed = person1.getMyWishList().get(0);
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
        Person person1 = createPersonWith2IdeaWishlist();
        Person person2 = new Person();
        WishlistIdea ideaClaimed = person1.getMyWishList().get(0);
        WishlistIdea ideaNotClaimed = person1.getMyWishList().get(1);
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
        Person person1 = createPersonWith2IdeaWishlist();
        Person person2 = new Person();
        person1.claimGiftIdeaInWishlist(person2, person1.getMyWishList().get(0));

        WishlistIdea newIdea = new WishlistIdea(randomId());
        newIdea.setName("Sonic Screwdriver");
        person1.addIdeaToWishList(newIdea);

        Assert.assertTrue(person1.wishlistContainsIdea(newIdea));
    }

    @Test
    public void unclaimIdea() {
        Person person1 = createPersonWith2IdeaWishlist();
        Person person2 = new Person();
        WishlistIdea ideaClaimed = person1.getMyWishList().get(1);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);
        person1.removeClaimFromWishlist(ideaClaimed);

        Assert.assertFalse(person1.findTheirItemInWishlist(ideaClaimed).isClaimed(), "Should not be claimed anymore.");
    }

    @Test
    public void claimAfterUnclaimed() {
        Person person1 = createPersonWith2IdeaWishlist();
        Person person2 = new Person();
        WishlistIdea ideaClaimed = person1.getMyWishList().get(1);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);
        person1.removeClaimFromWishlist(ideaClaimed);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);

        Assert.assertEquals(person1.findTheirItemInWishlist(ideaClaimed).getClaimedBy(), person2,
                "Should be claimed again by person2.");
    }

    @Test
    public void rolloverWishlist() {
        Person person1 = createPersonWith2IdeaWishlist();
        Person person2 = new Person();
        WishlistIdea ideaClaimed = person1.getMyWishList().get(0);
        WishlistIdea ideaNotClaimed = person1.getMyWishList().get(1);
        person1.claimGiftIdeaInWishlist(person2, ideaClaimed);
        person1.rolloverWishlist();

        Assert.assertFalse(person1.wishlistContainsIdea(ideaClaimed), "Claimed item should no longer be in wishlist.");
        Assert.assertTrue(person1.wishlistContainsIdea(ideaNotClaimed), "Unclaimed item should stay in wishlist.");

        Assert.assertTrue(person1.proposedListContainsIdea(ideaClaimed), "Claimed item should now be in proposed list.");
        Assert.assertFalse(person1.proposedListContainsIdea(ideaNotClaimed), "Unclaimed item should not be in proposed list.");
    }

    @Test
    public void acceptGiftFromProposedList() {
        Person person = createPersonWith2IdeaWishlist2IdeaProposedList();
        WishlistIdea ideaAccepted = person.getMyProposedList().get(0).getIdea();
        WishlistIdea ideaNotAccepted = person.getMyProposedList().get(1).getIdea();
        person.acceptIdea(ideaAccepted);

        Assert.assertFalse(person.proposedListContainsIdea(ideaAccepted), "Accepted item should no longer be in proposed list.");
        Assert.assertTrue(person.proposedListContainsIdea(ideaNotAccepted), "Unaccepted item should still be in proposed list.");

        Assert.assertTrue(person.receivedListContainsIdea(ideaAccepted), "Accepted item should be in received items.");
        Assert.assertFalse(person.receivedListContainsIdea(ideaNotAccepted), "Unaccepted item should not be in received items.");
    }

    @Test
    public void denyGiftFromProposedList() {
        Person person = createPersonWith2IdeaWishlist2IdeaProposedList();
        WishlistIdea ideaDenied = person.getMyProposedList().get(1).getIdea();
        WishlistIdea ideaNotDenied = person.getMyProposedList().get(0).getIdea();
        person.denyIdea(ideaDenied);

        Assert.assertFalse(person.proposedListContainsIdea(ideaDenied), "Denied item should no longer be in proposed list.");
        Assert.assertTrue(person.proposedListContainsIdea(ideaNotDenied), "Undenied item should still be in proposed list.");

        Assert.assertTrue(person.wishlistContainsIdea(ideaDenied), "Denied item should be in wishlist.");
        Assert.assertFalse(person.findTheirItemInWishlist(ideaDenied).isClaimed());
    }

    private Person createPersonWith2IdeaWishlist() {
        Person person = new Person();
        WishlistIdea idea1 = new WishlistIdea(randomId());
        idea1.setName("Raspberry Pi");
        person.addIdeaToWishList(idea1);
        WishlistIdea idea2 = new WishlistIdea(randomId());
        idea2.setName("Arduino");
        person.addIdeaToWishList(idea2);

        return person;
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

    private Person createPersonWith2IdeaWishlist2IdeaProposedList() {
        Person person = createPersonWith2IdeaWishlist();
        Person personClaiming = new Person();
        WishlistIdea proposed1 = new WishlistIdea(randomId());
        proposed1.setName("Sonic Screwdriver");
        person.addIdeaToWishList(proposed1);
        person.claimGiftIdeaInWishlist(personClaiming, proposed1);
        WishlistIdea proposed2 = new WishlistIdea(randomId());
        proposed2.setName("Light Saber");
        person.addIdeaToWishList(proposed2);
        person.claimGiftIdeaInWishlist(personClaiming, proposed2);
        person.rolloverWishlist();

        return person;
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
