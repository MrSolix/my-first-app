package by.dutov.jee.repository.person.orm;

import by.dutov.jee.people.Admin;
import org.springframework.stereotype.Repository;

import static by.dutov.jee.repository.ConstantsClass.GET_ADMIN_BY_ID;
import static by.dutov.jee.repository.ConstantsClass.GET_ADMIN_BY_NAME;
import static by.dutov.jee.repository.ConstantsClass.GET_ALL_ADMINS;

@Repository
public class AdminDaoSpringOrm extends AbstractPersonDaoSpringOrm {

    public AdminDaoSpringOrm() {
        clazz = Admin.class;
    }

    @Override
    protected String findAllJpql() {
        return GET_ALL_ADMINS;
    }

    @Override
    protected String namedQueryByName() {
        return GET_ADMIN_BY_NAME;
    }

    @Override
    protected String namedQueryById() {
        return GET_ADMIN_BY_ID;
    }
}
