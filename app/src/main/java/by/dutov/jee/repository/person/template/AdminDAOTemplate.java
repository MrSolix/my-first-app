package by.dutov.jee.repository.person.template;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.ConstantsClass;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class AdminDAOTemplate extends AbstractPersonDAOTemplate {

    public AdminDAOTemplate() {
        this.rowMapper = (rs, rowNum) -> new Admin()
                .withId(rs.getInt(ConstantsClass.U_ID))
                .withUserName(rs.getString(ConstantsClass.U_USER_NAME))
                .withBytePass(rs.getBytes(ConstantsClass.U_PASS))
                .withSalt(rs.getBytes(ConstantsClass.U_SALT))
                .withName(rs.getString(ConstantsClass.U_NAME))
                .withAge(rs.getInt(ConstantsClass.U_AGE));
    }

    @Override
    protected String setRole() {
        return Role.ADMIN.getType();
    }

    @Override
    protected Optional<Person> getUser(String name, Integer id) {
        return Optional.empty();
    }

    @Override
    protected Set<Group> getGroup(Integer userId) {
        return null;
    }
}
