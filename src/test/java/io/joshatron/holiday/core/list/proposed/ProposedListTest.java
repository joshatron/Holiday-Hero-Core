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

        Assert.assertFalse(list.containsIdea(idea.getId()), "The idea should not be added yet.");
        list.addIdea(idea);
        Assert.assertTrue(list.containsIdea(idea.getId()), "The idea should now be added.");
    }

    @Test
    public void addItemAgain() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());

        list.addIdea(idea);

        try {
            list.addIdea(idea);
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
        list.addIdeas(ideas);

        Assert.assertFalse(list.containsIdea(randomId()), "Should not contain random idea.");
        Assert.assertTrue(list.containsIdea(ideas.get(0).getId()), "Should contain first idea.");
        Assert.assertTrue(list.containsIdea(ideas.get(1).getId()), "Should contain second idea.");
        Assert.assertTrue(list.containsIdea(ideas.get(2).getId()), "Should contain third idea.");
    }

    @Test
    public void addItemsAgain() {
        ProposedList list = new ProposedList(randomId(), randomId());
        List<ProposedIdea> ideas = new ArrayList<>();
        ideas.add(new ProposedIdea(randomId()));
        ideas.add(new ProposedIdea(randomId()));
        ideas.add(new ProposedIdea(randomId()));
        list.addIdeas(ideas);

        List<ProposedIdea> ideasWithCopy = new ArrayList<>();
        ideasWithCopy.add(ideas.get(2));
        ideasWithCopy.add(new ProposedIdea(randomId()));
        ideasWithCopy.add(ideas.get(1));

        list.addIdeas(ideasWithCopy);
        Assert.assertTrue(list.containsIdea(ideasWithCopy.get(1).getId()), "Should added just the one noncopy.");
        Assert.assertEquals(list.getList().size(), 4, "Should not have added the copies again.");
    }

    @Test
    public void acceptIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea ideaAccepted = new ProposedIdea(randomId());
        ProposedIdea ideaIgnored = new ProposedIdea(randomId());
        list.addIdea(ideaAccepted);
        list.addIdea(ideaIgnored);

        ProposedIdea ideaAcceptedBack = list.acceptIdea(ideaAccepted.getId());
        Assert.assertEquals(ideaAcceptedBack, ideaAccepted, "Idea returned should be the same as accepted.");
        Assert.assertTrue(list.containsIdea(ideaIgnored.getId()), "Ignored idea should still be in list.");
        Assert.assertFalse(list.containsIdea(ideaAccepted.getId()), "Accepted item should no longer be in list.");
    }

    @Test
    public void acceptNonPresentIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());
        list.addIdea(idea);

        try {
            list.acceptIdea(randomId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be item not found.");
            Assert.assertTrue(list.containsIdea(idea.getId()), "List should still have added item.");
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
        list.addIdea(ideaDenied);
        list.addIdea(ideaIgnored);

        list.denyIdea(ideaDenied.getId());
        Assert.assertEquals(list.getList().size(), 1, "List should now only have one item.");
        Assert.assertFalse(list.containsIdea(ideaDenied.getId()), "List should no longer have idea.");
    }

    @Test
    public void denyNonPresentIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());
        list.addIdea(idea);

        try {
            list.denyIdea(randomId());
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be item not found.");
            Assert.assertTrue(list.containsIdea(idea.getId()), "List should still have added item.");
            Assert.assertEquals(list.getList().size(), 1, "List should still only have one item.");
        } catch (Exception e) {
            Assert.fail("Should have been a list exception.");
        }
    }

    @Test
    public void getIdea() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea idea = new ProposedIdea(randomId());
        list.addIdea(idea);


        Assert.assertEquals(list.getIdea(idea.getId()), idea, "Should have gotten idea back.");
    }

    @Test
    public void getIdeaNotPresent() {
        ProposedList list = new ProposedList(randomId(), randomId());

        try {
            list.getIdea(randomId());
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
        list.addIdea(ideaToUpdate);

        ProposedIdea updatedIdea = new ProposedIdea(ideaToUpdate.getId());
        updatedIdea.setName("Arduino");

        Assert.assertNotEquals(list.getIdea(updatedIdea.getId()), updatedIdea, "Ideas should be different for now.");
        list.updateIdea(updatedIdea);
        Assert.assertEquals(list.getIdea(updatedIdea.getId()), updatedIdea, "Ideas should now be the same.");
    }

    @Test
    public void updateIdeaNotPresent() {
        ProposedList list = new ProposedList(randomId(), randomId());
        ProposedIdea ideaNotUpdated = new ProposedIdea(randomId());
        list.addIdea(ideaNotUpdated);

        ProposedIdea updatedIdea = new ProposedIdea(randomId());
        updatedIdea.setName("Raspberry Pi");

        try {
            list.updateIdea(updatedIdea);
            Assert.fail("Should have thrown an exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_NOT_FOUND, "Reason should be idea not found.");
            Assert.assertEquals(list.getList().size(), 1, "List should still only have one element.");
            Assert.assertTrue(list.containsIdea(ideaNotUpdated.getId()), "Should still contain not updated idea.");
            Assert.assertFalse(list.containsIdea(updatedIdea.getId()), "Should not contain idea update.");
        } catch (Exception e) {
            Assert.fail("Should have thrown a list exception.");
        }
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
