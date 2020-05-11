package io.joshatron.holiday.core.store;

import io.joshatron.holiday.core.Person;
import io.joshatron.holiday.core.exception.PersonOperationException;
import io.joshatron.holiday.core.exception.PersonOperationExceptionReason;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<Person> foundPerson = people.stream().filter(p -> p.getId().equals(personId)).findFirst();

        if(foundPerson.isPresent()) {
            return foundPerson.get();
        }

        throw new PersonOperationException(PersonOperationExceptionReason.PERSON_NOT_FOUND);
    }

    @Override
    public boolean contains(String personId) {
        try {
            find(personId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
