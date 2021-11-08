package by.dutov.jee.repository.person;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PersonDAO<T> {

    abstract void sqlForFind(String sql) throws SQLException;

    abstract T save(T t);

    public Optional<? extends Person> find(String name) {
        return null;
    }

    abstract Optional<? extends Person> find(Integer id);

    abstract T update(Integer id, T t);

    abstract T remove(T t);

    abstract List<? extends Person> findAll(Role role);

    abstract List<? extends Person> findAll();

    List<T> resultSetToPerson(ResultSet rs) throws SQLException {
        String[] aliases = aliases();
        Map<Integer, T> personMap = new ConcurrentHashMap<>();
        Map<Integer, Group> groupMap = new ConcurrentHashMap<>();
        while (rs.next()) {
            final int gId = rs.getInt(aliases[0]);
            final int pId = rs.getInt(aliases[1]);
            final String pUserName = rs.getString(aliases[2]);
            final byte[] pPass = rs.getBytes(aliases[3]);
            final byte[] pSalt = rs.getBytes(aliases[4]);
            final String pName = rs.getString(aliases[5]);
            final int pAge = rs.getInt(aliases[6]);
            final Role role = Role.getTypeByStr(aliases[7]);

//            personMap.putIfAbsent(pId, )
        }
        return null;
    }

    abstract String[] aliases();
}
