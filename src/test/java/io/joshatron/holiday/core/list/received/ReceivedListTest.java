package io.joshatron.holiday.core.list.received;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class ReceivedListTest {
    @Test
    public void createList() {
        String id = randomId();
        String owner = randomId();
        ReceivedList list = new ReceivedList(id, owner);

        Assert.assertEquals(list.getId(), id);
        Assert.assertEquals(list.getOwner(), owner);
        Assert.assertTrue(list.getList().isEmpty());
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
