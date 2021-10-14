package by.dutov.jee.dao;

import by.dutov.jee.people.Person;

import java.util.HashMap;
import java.util.Map;

public class PersonDAOImpl implements DAO<Person> {
    private Map<String, Person> accounts = new HashMap<>();

    @Override
    public void create(String name, Person person) {
        accounts.put(name, person);
    }

    @Override
    public Person read(String name) {
        return accounts.get(name);
    }

    @Override
    public Person read(String name, String password) {
        Person person = accounts.get(name);
        if (person != null && person.getPassword().equals(password)){
            return person;
        }
        return null;
    }

    @Override
    public void update(String name, Person person) {
        accounts.replace(name, person);
    }

    @Override
    public void delete(String name) {
        accounts.remove(name);
    }
}
