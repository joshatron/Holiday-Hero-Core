package io.joshatron.holiday.core;

import io.joshatron.holiday.core.exception.PersonOperationException;
import io.joshatron.holiday.core.exception.PersonOperationExceptionReason;
import io.joshatron.holiday.core.store.SimplePersonStore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class PeopleTest {

    @Test
    public void addOnePersonToNetwork() {
        People network = new People(new SimplePersonStore());
        Person toAdd = new Person(randomId());
        network.addPerson(toAdd);

        Assert.assertEquals(network.findPerson(toAdd.getId()), toAdd, "Person should be added.");
    }

    @Test
    public void addMultiplePeopleToNetwork() {
        People network = new People(new SimplePersonStore());
        Person firstAdd = new Person(randomId());
        Person secondAdd = new Person(randomId());
        network.addPerson(firstAdd);
        network.addPerson(secondAdd);

        Assert.assertEquals(network.findPerson(firstAdd.getId()), firstAdd, "Person should be added.");
        Assert.assertEquals(network.findPerson(secondAdd.getId()), secondAdd, "Person should be added.");
    }

    @Test
    public void findNonexistantUserInNetwork() {
        People network = new People(new SimplePersonStore());
        Person person = new Person(randomId());
        network.addPerson(person);

        try {
            network.findPerson(randomId());
            Assert.fail("Should have thrown an exception.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.PERSON_NOT_FOUND);
        } catch (Exception e) {
            Assert.fail("Should have been a person operation exception.");
        }
    }

    @Test
    public void checkIfUserPresent() {
        People network = new People(new SimplePersonStore());
        Person person = new Person(randomId());

        Assert.assertFalse(network.hasPerson(person.getId()));
        network.addPerson(person);
        Assert.assertTrue(network.hasPerson(person.getId()));
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
