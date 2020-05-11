package io.joshatron.holiday.core.store;

import io.joshatron.holiday.core.Person;

public interface PersonStore {
    void add(Person person);
    Person find(String personId);
}
