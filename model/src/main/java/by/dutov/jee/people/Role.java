package by.dutov.jee.people;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum Role {
    STUDENT("STUDENT"),
    TEACHER("TEACHER"),
    ADMIN("ADMIN");

    private final String type;

    private static Map<String, Role> value2Enum = initValue2Enum();

    private static Map<Role, String> enum2Value = initEnum2Value();

    Role(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private static Map<String, Role> initValue2Enum() {
        Role[] values = Role.values();
        Map<String, Role> map = new HashMap<>(values.length);
        for (Role enumElement : values) {
            map.put(enumElement.type, enumElement);
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<Role, String> initEnum2Value() {
        Map<Role, String> map = new EnumMap<>(Role.class);
        Role[] values = Role.values();
        for (Role enumElement : values) {
            map.put(enumElement, enumElement.type);
        }
        return Collections.unmodifiableMap(map);
    }
    public static Role getTypeByStr(String str) {
        return value2Enum.get(str);
    }

    public static String getStrByType(Role type) {
        return enum2Value.get(type);
    }

}
