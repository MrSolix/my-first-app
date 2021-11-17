package by.dutov.jee.repository.person;

import by.dutov.jee.people.Person;

import java.util.List;
import java.util.Optional;

public interface PersonDAOInterface<T> {
    T save(T t);
    Optional<? extends Person> find(Integer id);
    Optional<? extends Person> find(String name);
    T update(Integer id, T t);
    T remove(T t);
    List<? extends Person> findAll();
}
