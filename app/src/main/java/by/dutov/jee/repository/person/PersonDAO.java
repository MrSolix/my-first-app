package by.dutov.jee.repository.person;

import by.dutov.jee.people.Person;

import java.util.List;
import java.util.Optional;

public interface PersonDAO<T> {
    T save(T t);
    Optional<? extends Person> find(String name);
    Optional<? extends Person> find(Integer id);
    T update(String name, T t);
    T remove(T t);
    List<T> findAll();
}