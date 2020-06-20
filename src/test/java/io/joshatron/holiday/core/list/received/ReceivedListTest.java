package io.joshatron.holiday.core.list.received;

import org.testng.annotations.Test;

import java.util.UUID;

public class ReceivedListTest {
    @Test
    public void createList() {
        ReceivedList list = new ReceivedList(randomId(), randomId());
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
