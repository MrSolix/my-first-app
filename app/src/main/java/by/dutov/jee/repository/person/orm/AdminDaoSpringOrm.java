package by.dutov.jee.repository.person.orm;

import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDaoSpringOrm extends AbstractPersonDaoSpringOrm {

    public AdminDaoSpringOrm() {
        clazz = Admin.class;
    }

    @Override
    protected String findAllJpql() {
        return "from Admin u where u.role = 'ADMIN'";
    }

    @Override
    protected String namedQueryByName() {
        return "findAdminByName";
    }

    @Override
    protected String namedQueryById() {
        return "findAdminById";
    }
}
