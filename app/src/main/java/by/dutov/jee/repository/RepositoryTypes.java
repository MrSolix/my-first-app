package by.dutov.jee.repository;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.repository.person.jpa.PersonDaoJpa;
import by.dutov.jee.repository.person.memory.PersonDAOInMemory;
import by.dutov.jee.repository.person.postgres.PersonDAOPostgres;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum  RepositoryTypes {
    MEMORY("memory", PersonDAOInMemory.getInstance()),
    POSTGRES("postgres", PersonDAOPostgres.getInstance()),
    JPA("jpa", PersonDaoJpa.getInstance());

    private final String type;
    private final PersonDAOInterface<Person> dao;

    private static Map<String, RepositoryTypes> value2Enum = initValue2Enum();

    private static Map<RepositoryTypes, String> enum2Value = initEnum2Value();

    private static Map<RepositoryTypes, PersonDAOInterface<Person>> enum2Dao = initEnum2Dao();

    RepositoryTypes(String type, PersonDAOInterface<Person> dao) {
        this.type = type;
        this.dao = dao;
    }

    public String getType() {
        return type;
    }

    public PersonDAOInterface<Person> getDao() {
        return dao;
    }

    private static Map<RepositoryTypes, PersonDAOInterface<Person>> initEnum2Dao() {
        RepositoryTypes[] values = RepositoryTypes.values();
        Map<RepositoryTypes, PersonDAOInterface<Person>> map = new HashMap<>(values.length);
        for (RepositoryTypes enumElement : values) {
            map.put(enumElement, enumElement.getDao());
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<String, RepositoryTypes> initValue2Enum() {
        RepositoryTypes[] values = RepositoryTypes.values();
        Map<String, RepositoryTypes> map = new HashMap<>(values.length);
        for (RepositoryTypes enumElement : values) {
            map.put(enumElement.type, enumElement);
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<RepositoryTypes, String> initEnum2Value() {
        Map<RepositoryTypes, String> map = new EnumMap<>(RepositoryTypes.class);
        RepositoryTypes[] values = RepositoryTypes.values();
        for (RepositoryTypes enumElement : values) {
            map.put(enumElement, enumElement.type);
        }
        return Collections.unmodifiableMap(map);
    }

    public static RepositoryTypes getTypeByStr(String str) {
        return value2Enum.get(str);
    }

    public static String getStrByType(RepositoryTypes type) {
        return enum2Value.get(type);
    }

    public static PersonDAOInterface<Person> getDaoByType(RepositoryTypes type) {
        return enum2Dao.get(type);
    }
}
