package io.joshatron.holiday.core.store;

import io.joshatron.holiday.core.Person;

import java.util.ArrayList;
import java.util.List;

public class SimplePersonStore implements PersonStore {
    private List<Person> people;

    public SimplePersonStore() {
        people = new ArrayList<>();
    }

    @Override
    public void add(Person person) {
        people.add(person);
    }

    @Override
    public Person find(String personId) {
        return people.stream().filter(p -> p.getId().equals(personId)).findFirst().get();
    }
}
