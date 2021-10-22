package by.dutov.jee.dao;

public interface DAO<T> {
    void create(T t);
    T read(String name);
    T read(String name, String password);
    void update(String name, T t);
    void delete(String name);
}
