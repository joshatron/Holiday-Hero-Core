package io.joshatron.holiday.core.list;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class GenericListTest {
    @Test
    public void createList() {
        String id = randomId();
        String owner = randomId();
        GenericList<GenericItem> list = new GenericList<>(id, owner);
        Assert.assertEquals(list.getId(), id, "List ID does not match.");
        Assert.assertEquals(list.getOwner(), owner, "List owner does not match.");
        Assert.assertTrue(list.getList().isEmpty());
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
