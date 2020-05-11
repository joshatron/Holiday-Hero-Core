package io.joshatron.holiday.core;

import io.joshatron.holiday.core.exception.PersonOperationException;
import io.joshatron.holiday.core.exception.PersonOperationExceptionReason;
import io.joshatron.holiday.core.store.SimplePersonStore;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PersonNetworkTest {

    @Test
    public void addOnePersonToNetwork() {
        PersonNetwork network = new PersonNetwork(new SimplePersonStore());
        Person toAdd = new Person("1");
        network.addPerson(toAdd);

        Assert.assertEquals(network.findPerson(toAdd.getId()), toAdd, "Person should be added.");
    }

    @Test
    public void addMultiplePeopleToNetwork() {
        PersonNetwork network = new PersonNetwork(new SimplePersonStore());
        Person firstAdd = new Person("1");
        Person secondAdd = new Person("2");
        network.addPerson(firstAdd);
        network.addPerson(secondAdd);

        Assert.assertEquals(network.findPerson(firstAdd.getId()), firstAdd, "Person should be added.");
        Assert.assertEquals(network.findPerson(secondAdd.getId()), secondAdd, "Person should be added.");
    }

    @Test
    public void findNonexistantUserInNetwork() {
        PersonNetwork network = new PersonNetwork(new SimplePersonStore());
        Person person = new Person("1");
        network.addPerson(person);

        try {
            network.findPerson("2");
            Assert.fail("Should have thrown an exception.");
        } catch (PersonOperationException e) {
            Assert.assertEquals(e.getReason(), PersonOperationExceptionReason.PERSON_NOT_FOUND);
        } catch (Exception e) {
            Assert.fail("Should have been a person operation exception.");
        }
    }

    @Test
    public void checkIfUserPresent() {
        PersonNetwork network = new PersonNetwork(new SimplePersonStore());
        Person person = new Person("1");

        Assert.assertFalse(network.hasPerson(person.getId()));
        network.addPerson(person);
        Assert.assertTrue(network.hasPerson(person.getId()));
    }
}
