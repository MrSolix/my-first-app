package by.dutov.jee.repository.group;

import java.util.List;
import java.util.Optional;

public interface GroupDAOInterface<T> {
    T save(T t);
    Optional<T> find(Integer id);
    T update(Integer id, T t);
    T remove(T t);
    List<T> findAll();
}
