package io.joshatron.holiday.core;

import io.joshatron.holiday.core.store.PersonStore;

public class PersonNetwork {
    PersonStore store;

    public PersonNetwork(PersonStore store) {
        this.store = store;
    }

    public void addPerson(Person person) {
        store.add(person);
    }

    public Person findPerson(String personId) {
        return store.find(personId);
    }
}
