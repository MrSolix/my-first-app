package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Student;

import javax.sql.DataSource;

public class AdminDaoJpa  extends AbstractPersonDaoJpa<Admin> {
    private static volatile AdminDaoJpa instance;

    public AdminDaoJpa() {
        //singleton
    }

    public static AdminDaoJpa getInstance() {
        if (instance == null) {
            synchronized (AdminDaoJpa.class) {
                if (instance == null) {
                    instance = new AdminDaoJpa();
                }
            }
        }
        return instance;
    }

    @Override
    protected Class<Admin> getType() {
        return Admin.class;
    }

    @Override
    protected String findAllJpql() {
        return "from Admin";
    }

    @Override
    protected String nameNamedQuery() {
        return "findAdmin";
    }
}
