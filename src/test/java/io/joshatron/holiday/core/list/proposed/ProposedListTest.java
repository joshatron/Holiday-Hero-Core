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
    public void addItem() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());

        Assert.assertFalse(list.containsItem(idea.getId()), "The idea should not be added yet.");
        list.addItem(idea);
        Assert.assertTrue(list.containsItem(idea.getId()), "The idea should now be added.");
    }

    @Test
    public void addItemAgain() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());

        list.addItem(idea);

        try {
            list.addItem(idea);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_ALREADY_ADDED);
            Assert.assertEquals(list.getList().size(), 1);
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void addItems()  {
        ProposedList list = new ProposedList(randomId(), randomId());
        List<ProposedIdea> ideas = new ArrayList<>();
        ideas.add(new ProposedIdea(randomId()));
        ideas.add(new ProposedIdea(randomId()));
        ideas.add(new ProposedIdea(randomId()));
        list.addItems(ideas);

        Assert.assertFalse(list.containsItem(randomId()), "Should not contain random idea.");
        Assert.assertTrue(list.containsItem(ideas.get(0).getId()), "Should contain first idea.");
        Assert.assertTrue(list.containsItem(ideas.get(1).getId()), "Should contain second idea.");
        Assert.assertTrue(list.containsItem(ideas.get(2).getId()), "Should contain third idea.");
    }

    @Test
    public void addItemsAgain() {
        ProposedList list = new ProposedList(randomId(), randomId());
        List<ProposedIdea> ideas = new ArrayList<>();
        ideas.add(new ProposedIdea(randomId()));
        ideas.add(new ProposedIdea(randomId()));
        ideas.add(new ProposedIdea(randomId()));
        list.addItems(ideas);

        List<ProposedIdea> ideasWithCopy = new ArrayList<>();
        ideasWithCopy.add(ideas.get(2));
        ideasWithCopy.add(new ProposedIdea(randomId()));
        ideasWithCopy.add(ideas.get(1));

        list.addItems(ideasWithCopy);
        Assert.assertTrue(list.containsItem(ideasWithCopy.get(1).getId()), "Should added just the one noncopy.");
        Assert.assertEquals(list.getList().size(), 4, "Should not have added the copies again.");
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

    @Test
    public void getIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());
        list.addItem(idea);


        Assert.assertEquals(list.getItem(idea.getId()), idea, "Should have gotten idea back.");
    }

    @Test
    public void getIdeaNotPresent() {
        ProposedList list = new ProposedList(randomId(), randomId());

        try {
            list.getItem(randomId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be item not found.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    @Test
    public void updateIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea ideaToUpdate = new ProposedIdea(randomId());
        ideaToUpdate.setName("Raspberry Pi");
        list.addItem(ideaToUpdate);

        ProposedIdea updatedIdea = new ProposedIdea(ideaToUpdate.getId());
        updatedIdea.setName("Arduino");

        Assert.assertNotEquals(list.getItem(updatedIdea.getId()), updatedIdea, "Ideas should be different for now.");
        list.updateItem(updatedIdea);
        Assert.assertEquals(list.getItem(updatedIdea.getId()), updatedIdea, "Ideas should now be the same.");
    }

    @Test
    public void updateIdeaNotPresent() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea ideaNotUpdated = new ProposedIdea(randomId());
        list.addItem(ideaNotUpdated);

        ProposedIdea updatedIdea = new ProposedIdea(randomId());
        updatedIdea.setName("Raspberry Pi");

        try {
            list.updateItem(updatedIdea);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be idea not found.");
            Assert.assertEquals(list.getList().size(), 1, "List should still only have one element.");
            Assert.assertTrue(list.containsItem(ideaNotUpdated.getId()), "Should still contain not updated idea.");
            Assert.assertFalse(list.containsItem(updatedIdea.getId()), "Should not contain idea update.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
