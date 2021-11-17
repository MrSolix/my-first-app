package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.person.PersonDAOInterface;

import java.util.List;
import java.util.Optional;

public abstract class AbstractPersonDaoJpa<T extends Person> implements PersonDAOInterface<T> {

    @Override
    public T save(T t) {
        return null;
    }

    @Override
    public Optional<? extends Person> find(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<? extends Person> find(String name) {
        return Optional.empty();
    }

    @Override
    public T update(Integer id, T t) {
        return null;
    }

    @Override
    public T remove(T t) {
        return null;
    }

    @Override
    public List<? extends Person> findAll() {
        return null;
    }
}
