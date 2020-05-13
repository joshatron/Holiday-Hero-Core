package io.joshatron.holiday.core;

import io.joshatron.holiday.core.store.PersonStore;

public class People {
    PersonStore store;

    public People(PersonStore store) {
        this.store = store;
    }

    public void addPerson(Person person) {
        store.add(person);
    }

    public Person findPerson(String personId) {
        return store.find(personId);
    }

    public boolean hasPerson(String personId) {
        return store.contains(personId);
    }
}
