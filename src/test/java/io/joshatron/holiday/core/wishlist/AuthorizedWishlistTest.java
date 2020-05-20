package io.joshatron.holiday.core.wishlist;

import io.joshatron.holiday.core.wishlist.exception.WishlistException;
import io.joshatron.holiday.core.wishlist.exception.WishlistExceptionReason;
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
        authorizedList.addIdea(owner, firstIdea);
        Assert.assertTrue(authorizedList.containsIdea(owner, firstIdea.getId()), "Should have added idea.");

        WishlistIdea secondIdea = new WishlistIdea(randomId());
        try {
            authorizedList.addIdea(randomId(), secondIdea);
            Assert.fail("Should have thrown exception.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.USER_NOT_AUTHORIZED);
            Assert.assertFalse(authorizedList.containsIdea(owner, secondIdea.getId()));
        } catch (Exception e) {
            Assert.fail("Should have thrown a wishlist exception.");
        }
    }

    @Test
    public void anyoneCanCheckIfListContainsIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);

        Assert.assertTrue(authorizedList.containsIdea(randomId(), idea.getId()), "Should say the idea is found.");
        Assert.assertFalse(authorizedList.containsIdea(randomId(), randomId()), "Should not have found the idea.");
    }

    @Test
    public void anyoneCanGetIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);

        Assert.assertEquals(authorizedList.getIdea(randomId(), idea.getId()), idea, "Should have found the idea.");
    }

    @Test
    public void ownerShouldntSeeClaimInfo() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);
        authorizedList.claimIdea(randomId(), idea.getId());

        WishlistIdea returnedIdeaOwner = authorizedList.getIdea(owner, idea.getId());
        WishlistIdea returnedIdeaRandom = authorizedList.getIdea(randomId(), idea.getId());

        Assert.assertFalse(returnedIdeaOwner.isClaimed(), "From owner perspective, should not be claimed.");
        Assert.assertTrue(returnedIdeaRandom.isClaimed(), "From other perspective, should be claimed.");
    }

    @Test
    public void getIdeaShouldReturnACopy() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);

        WishlistIdea firstGrabbed = authorizedList.getIdea(randomId(), idea.getId());
        WishlistIdea secondGrabbed = authorizedList.getIdea(randomId(), idea.getId());
        firstGrabbed.setId(randomId());

        Assert.assertNotEquals(firstGrabbed, secondGrabbed, "Changing one shouldn't change other.");
    }

    @Test
    public void onlyOwnerCanRemoveIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);

        try {
            authorizedList.removeIdea(randomId(), idea.getId());
            Assert.fail("Should have thrown an exception.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
            Assert.assertTrue(authorizedList.containsIdea(randomId(), idea.getId()), "Should not have removed the idea.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a wishlist exception.");
        }

        authorizedList.removeIdea(owner, idea.getId());
        Assert.assertFalse(authorizedList.containsIdea(owner, idea.getId()), "Idea should be removed.");
    }

    @Test
    public void anyoneButOwnerCanClaimIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);

        try {
            authorizedList.claimIdea(owner, idea.getId());
            Assert.fail("Should have thrown an exception");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a wishlist exception.");
        }
        Assert.assertFalse(authorizedList.getIdea(owner, idea.getId()).isClaimed(), "Idea should not be claimed yet.");

        authorizedList.claimIdea(randomId(), idea.getId());
        Assert.assertTrue(authorizedList.getIdea(randomId(), idea.getId()).isClaimed(), "Idea should be claimed now.");
    }

    @Test
    public void onlyClaimerCanUnclaimIdea() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);

        String claimer = randomId();
        authorizedList.claimIdea(claimer, idea.getId());

        try {
            authorizedList.unclaimIdea(randomId(), idea.getId());
            Assert.fail("Should have thrown an exception.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
            Assert.assertTrue(authorizedList.getIdea(randomId(), idea.getId()).isClaimed(), "The idea should still be claimed.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a wishlist exception.");
        }

        authorizedList.unclaimIdea(claimer, idea.getId());
        Assert.assertFalse(authorizedList.getIdea(randomId(), idea.getId()).isClaimed(), "The idea should now be unclaimed.");
    }

    @Test
    public void onlyOwnerCanRolloverList() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);

        authorizedList.claimIdea(randomId(), idea.getId());

        try {
            authorizedList.rollover(randomId());
            Assert.fail("Should have thrown an exception.");
        } catch (WishlistException e) {
            Assert.assertEquals(e.getReason(), WishlistExceptionReason.USER_NOT_AUTHORIZED, "The reason should be user not authorized.");
            Assert.assertTrue(authorizedList.containsIdea(randomId(), idea.getId()), "The idea should still be in list.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a wishlist exception.");
        }

        List<WishlistIdea> rolled = authorizedList.rollover(owner);
        Assert.assertEquals(rolled.get(0), idea, "Rolled idea should be the idea.");
        Assert.assertFalse(authorizedList.containsIdea(randomId(), idea.getId()), "The idea should no longer be in the list.");
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
        authorizedList.addIdea(owner, idea);

        Assert.assertEquals(authorizedList.getIdeas(randomId()), wishlist.getList(), "The lists should be identical.");
    }

    @Test
    public void whenOwnerGetsListThereAreNoClaims() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);
        authorizedList.claimIdea(randomId(), idea.getId());

        List<WishlistIdea> ideas = authorizedList.getIdeas(owner);
        Assert.assertFalse(ideas.get(0).isClaimed(), "The idea should not be claimed to the owner.");

        ideas = authorizedList.getIdeas(randomId());
        Assert.assertTrue(ideas.get(0).isClaimed(), "The idea should be claimed by a random user.");
    }

    @Test
    public void getFullListReturnsCopiesOfIdeas() {
        String owner = randomId();
        Wishlist wishlist = new Wishlist(randomId(), owner);
        AuthorizedWishlist authorizedList = new AuthorizedWishlist(wishlist);

        WishlistIdea idea = new WishlistIdea(randomId());
        authorizedList.addIdea(owner, idea);

        List<WishlistIdea> ideas = authorizedList.getIdeas(randomId());
        ideas.get(0).setId("NOT A REAL ID");

        Assert.assertNotEquals(ideas, wishlist.getList());
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
