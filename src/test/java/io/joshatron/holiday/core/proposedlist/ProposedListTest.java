package io.joshatron.holiday.core.proposedlist;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.UUID;

public class ProposedListTest {
    @Test
    public void createList() {
        String id = randomId();
        String owner = randomId();
        ProposedList list = new ProposedList(id, owner);

        Assert.assertEquals(list.getId(), id);
        Assert.assertEquals(list.getOwner(), owner);
        Assert.assertEquals(list.getList(), new ArrayList<>());
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
