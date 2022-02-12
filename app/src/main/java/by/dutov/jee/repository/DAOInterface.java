package by.dutov.jee.repository;

import java.util.List;
import java.util.Optional;

public interface DAOInterface<T> {
    T save(T t);

    Optional<T> find(Integer id);

    T update(Integer id, T t);

    T remove(T t);

    List<T> findAll();
}
