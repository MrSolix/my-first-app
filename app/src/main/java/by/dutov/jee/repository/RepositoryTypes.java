package by.dutov.jee.repository;

import by.dutov.jee.MyAppContext;
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
    MEMORY("memory"),
    POSTGRES("postgres"),
    JPA("jpa");

    private final String type;

    private static final Map<String, RepositoryTypes> value2Enum = initValue2Enum();

    private static final Map<RepositoryTypes, String> enum2Value = initEnum2Value();

    RepositoryTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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

}
