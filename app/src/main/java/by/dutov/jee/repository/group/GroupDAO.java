package by.dutov.jee.repository.group;

import by.dutov.jee.people.Student;

import java.util.List;
import java.util.Optional;

public abstract class GroupDAO<T> {
    abstract T save(T t);
    abstract Optional<T> find(Integer id);
    abstract T update(Integer id, T t);
    abstract T remove(T t);
    abstract List<T> findAll();
}
