package io.joshatron.holiday.core.proposedlist;

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

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
