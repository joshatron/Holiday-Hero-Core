package io.joshatron.holiday.core.list.proposed;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProposedListTest {
    @Test
    public void createList() {
        String id = randomId();
        String owner = randomId();
        ProposedList list = new ProposedList(id, owner);

        Assert.assertEquals(list.getId(), id, "Id should be as set.");
        Assert.assertEquals(list.getOwner(), owner, "Owner should be as set.");
        Assert.assertEquals(list.getList(), new ArrayList<>(), "There should be no items yet.");
    }

    @Test
    public void acceptIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea ideaAccepted = new ProposedIdea(randomId());
        ProposedIdea ideaIgnored = new ProposedIdea(randomId());
        list.addItem(ideaAccepted);
        list.addItem(ideaIgnored);

        ProposedIdea ideaAcceptedBack = list.acceptItem(ideaAccepted.getId());
        Assert.assertEquals(ideaAcceptedBack, ideaAccepted, "Idea returned should be the same as accepted.");
        Assert.assertTrue(list.containsItem(ideaIgnored.getId()), "Ignored idea should still be in list.");
        Assert.assertFalse(list.containsItem(ideaAccepted.getId()), "Accepted item should no longer be in list.");
    }

    @Test
    public void acceptNonPresentIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());
        list.addItem(idea);

        try {
            list.acceptItem(randomId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be item not found.");
            Assert.assertTrue(list.containsItem(idea.getId()), "List should still have added item.");
            Assert.assertEquals(list.getList().size(), 1, "List should still only have one item.");
        } catch (Exception e) {
            Assert.fail("Should have been a list exception.");
        }
    }
    
    @Test
    public void denyIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea ideaDenied = new ProposedIdea(randomId());
        ProposedIdea ideaIgnored = new ProposedIdea(randomId());
        list.addItem(ideaDenied);
        list.addItem(ideaIgnored);

        list.denyItem(ideaDenied.getId());
        Assert.assertEquals(list.getList().size(), 1, "List should now only have one item.");
        Assert.assertFalse(list.containsItem(ideaDenied.getId()), "List should no longer have idea.");
    }

    @Test
    public void denyNonPresentIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());
        list.addItem(idea);

        try {
            list.denyItem(randomId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be item not found.");
            Assert.assertTrue(list.containsItem(idea.getId()), "List should still have added item.");
            Assert.assertEquals(list.getList().size(), 1, "List should still only have one item.");
        } catch (Exception e) {
            Assert.fail("Should have been a list exception.");
        }
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
