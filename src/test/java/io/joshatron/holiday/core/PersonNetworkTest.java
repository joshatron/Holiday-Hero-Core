package io.joshatron.holiday.core;

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
}
