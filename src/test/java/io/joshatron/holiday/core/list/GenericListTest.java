package io.joshatron.holiday.core.list;

import io.joshatron.holiday.core.exception.ListException;
import io.joshatron.holiday.core.exception.ListExceptionReason;
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

    @Test
    public void addItem() {
        GenericList<GenericItem> list = new GenericList<>(randomId(), randomId());
        GenericItem item = new GenericItem(randomId());

        Assert.assertEquals(list.getList().size(), 0, "List should be empty to start.");
        Assert.assertFalse(list.containsItem(item.getId()), "List shouldn't contain item.");
        list.addItem(item);
        Assert.assertEquals(list.getList().size(), 1, "List should now have 1 item.");
        Assert.assertTrue(list.containsItem(item.getId()), "List should now contain item.");
    }

    @Test
    public void addItemAlreadyContains() {
        GenericList<GenericItem> list = new GenericList<>(randomId(), randomId());
        GenericItem item = new GenericItem(randomId());

        list.addItem(item);

        try {
            list.addItem(item);
            Assert.fail("Should have thrown exception.");
        } catch (ListException e) {
            Assert.assertEquals(e.getReason(), ListExceptionReason.ITEM_ALREADY_ADDED);
            Assert.assertEquals(list.getList().size(), 1, "List should now have 1 item.");
            Assert.assertTrue(list.containsItem(item.getId()), "List should now contain item.");
        } catch (Exception e) {
            Assert.fail("Should be list exception.");
        }
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
