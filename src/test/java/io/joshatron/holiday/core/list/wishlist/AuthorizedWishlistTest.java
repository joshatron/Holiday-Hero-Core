package io.joshatron.holiday.core.list.wishlist;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

public class AuthorizedWishlistTest {
    @Test
    public void onlyOwnerCanAddIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea firstIdea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, firstIdea);
        Assert.assertTrue(authorizedList.containsItem(owner, firstIdea.getId()), "Should have added idea.");

        WishlistIdea secondIdea = new WishlistIdea(randomId());
        try {
            authorizedList.addItem(randomId(), secondIdea);
            Assert.fail("Should have thrown exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.USER_NOT_AUTHORIZED);
            Assert.assertFalse(authorizedList.containsItem(owner, secondIdea.getId()));
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void addIdeaSavesCopy() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedWishlist = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        idea.setName("First Idea");
        authorizedWishlist.addItem(owner, idea);
        idea.setName("Other Idea");

        Assert.assertNotEquals(authorizedWishlist.getItem(owner, idea.getId()), idea, "Should be different objects.");
    }

    @Test
    public void anyoneCanCheckIfListContainsIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        Assert.assertTrue(authorizedList.containsItem(randomId(), idea.getId()), "Should say the idea is found.");
        Assert.assertFalse(authorizedList.containsItem(randomId(), randomId()), "Should not have found the idea.");
    }

    @Test
    public void anyoneCanGetIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        Assert.assertEquals(authorizedList.getItem(randomId(), idea.getId()), idea, "Should have found the idea.");
    }

    @Test
    public void ownerShouldntSeeClaimInfo() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);
        authorizedList.claimItem(randomId(), idea.getId());

        WishlistIdea returnedIdeaOwner = authorizedList.getItem(owner, idea.getId());
        WishlistIdea returnedIdeaRandom = authorizedList.getItem(randomId(), idea.getId());

        Assert.assertFalse(returnedIdeaOwner.isClaimed(), "From owner perspective, should not be claimed.");
        Assert.assertTrue(returnedIdeaRandom.isClaimed(), "From other perspective, should be claimed.");
    }

    @Test
    public void getIdeaShouldReturnACopy() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        WishlistIdea firstGrabbed = authorizedList.getItem(randomId(), idea.getId());
        WishlistIdea secondGrabbed = authorizedList.getItem(randomId(), idea.getId());
        firstGrabbed.setId(randomId());

        Assert.assertNotEquals(firstGrabbed, secondGrabbed, "Changing one shouldn't change other.");
    }

    @Test
    public void onlyOwnerCanUpdateIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea originalIdea = new WishlistIdea(randomId());
        originalIdea.setName("First Idea");
        authorizedList.addItem(owner, originalIdea);

        WishlistIdea updatedIdea = new WishlistIdea(originalIdea.getId());
        updatedIdea.setName("Updated Idea");

        try {
            authorizedList.updateItem(randomId(), updatedIdea);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
            Assert.assertEquals(authorizedList.getItem(owner, originalIdea.getId()).getName(), originalIdea.getName(), "Idea should not be updated.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }

        authorizedList.updateItem(owner, updatedIdea);
        Assert.assertEquals(authorizedList.getItem(owner, originalIdea.getId()).getName(), updatedIdea.getName(), "Idea should now be updated.");
    }

    @Test
    public void updateIdeaShouldMakeCopy() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea originalIdea = new WishlistIdea(randomId());
        originalIdea.setName("First Idea");
        authorizedList.addItem(owner, originalIdea);

        WishlistIdea updatedIdea = new WishlistIdea(originalIdea.getId());
        updatedIdea.setName("Updated Idea");
        authorizedList.updateItem(owner, updatedIdea);

        updatedIdea.setName("Wrong Idea");
        Assert.assertNotEquals(authorizedList.getItem(owner, originalIdea.getId()).getName(), updatedIdea.getName(), "Idea shouldn't have updated.");
    }

    @Test
    public void onlyOwnerCanRemoveIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        try {
            authorizedList.removeItem(randomId(), idea.getId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
            Assert.assertTrue(authorizedList.containsItem(randomId(), idea.getId()), "Should not have removed the idea.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }

        authorizedList.removeItem(owner, idea.getId());
        Assert.assertFalse(authorizedList.containsItem(owner, idea.getId()), "Idea should be removed.");
    }

    @Test
    public void anyoneButOwnerCanClaimIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        try {
            authorizedList.claimItem(owner, idea.getId());
            Assert.fail("Should have thrown an exception");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
        Assert.assertFalse(authorizedList.getItem(owner, idea.getId()).isClaimed(), "Idea should not be claimed yet.");

        authorizedList.claimItem(randomId(), idea.getId());
        Assert.assertTrue(authorizedList.getItem(randomId(), idea.getId()).isClaimed(), "Idea should be claimed now.");
    }

    @Test
    public void onlyClaimerCanUnclaimIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        String claimer = randomId();
        authorizedList.claimItem(claimer, idea.getId());

        try {
            authorizedList.unclaimItem(randomId(), idea.getId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
            Assert.assertTrue(authorizedList.getItem(randomId(), idea.getId()).isClaimed(), "The idea should still be claimed.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }

        authorizedList.unclaimItem(claimer, idea.getId());
        Assert.assertFalse(authorizedList.getItem(randomId(), idea.getId()).isClaimed(), "The idea should now be unclaimed.");
    }

    @Test
    public void onlyOwnerCanRolloverList() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        authorizedList.claimItem(randomId(), idea.getId());

        try {
            authorizedList.rollover(randomId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
            Assert.assertTrue(authorizedList.containsItem(randomId(), idea.getId()), "The idea should still be in list.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }

        List<WishlistIdea> rolled = authorizedList.rollover(owner);
        Assert.assertEquals(rolled.get(0).getId(), idea.getId(), "Rolled idea should be the idea.");
        Assert.assertFalse(authorizedList.containsItem(randomId(), idea.getId()), "The idea should no longer be in the list.");
    }

    @Test
    public void anyoneCanGetListId() {
        String listId = randomId();
        Wishlist wishlist = new Wishlist(listId, randomId());
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        Assert.assertEquals(authorizedList.getId(randomId()), listId, "The id returned should be correct.");
    }

    @Test
    public void anyoneCanGetFullList() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        Assert.assertEquals(authorizedList.getItems(randomId()), wishlist.getList(), "The lists should be identical.");
    }

    @Test
    public void whenOwnerGetsListThereAreNoClaims() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);
        authorizedList.claimItem(randomId(), idea.getId());

        List<WishlistIdea> ideas = authorizedList.getItems(owner);
        Assert.assertFalse(ideas.get(0).isClaimed(), "The idea should not be claimed to the owner.");

        ideas = authorizedList.getItems(randomId());
        Assert.assertTrue(ideas.get(0).isClaimed(), "The idea should be claimed by a random user.");
    }

    @Test
    public void getFullListReturnsCopiesOfIdeas() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addItem(owner, idea);

        List<WishlistIdea> ideas = authorizedList.getItems(randomId());
        ideas.get(0).setId("NOT A REAL ID");

        Assert.assertNotEquals(ideas, wishlist.getList());
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
