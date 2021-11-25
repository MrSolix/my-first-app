package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.person.postgres.AdminDAOPostgres;
import by.dutov.jee.repository.person.postgres.PersonDAOPostgres;
import by.dutov.jee.repository.person.postgres.StudentDAOPostgres;
import by.dutov.jee.repository.person.postgres.TeacherDAOPostgres;
import by.dutov.jee.service.exceptions.DataBaseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonDaoJpa extends AbstractPersonDaoJpa<Person> {
    private static volatile PersonDaoJpa instance;

    public PersonDaoJpa() {
        //singleton
    }

    public static PersonDaoJpa getInstance() {
        if (instance == null) {
            synchronized (PersonDaoJpa.class) {
                if (instance == null) {
                    instance = new PersonDaoJpa();
                }
            }
        }
        return instance;
    }

    @Override
    public Person save(Person person) {
        if (person instanceof Student) {
            return StudentDaoJpa.getInstance().save((Student) person);
        }
        if (person instanceof Teacher) {
            return TeacherDaoJpa.getInstance().save((Teacher) person);
        }
        if (person instanceof Admin) {
            return AdminDaoJpa.getInstance().save((Admin) person);
        }
        return null;
    }

    @Override
    public Optional<? extends Person> find(Integer id) {
        try {
            return StudentDaoJpa.getInstance().find(id);
        } catch (DataBaseException e) {
            try {
                return TeacherDaoJpa.getInstance().find(id);
            } catch (DataBaseException e1) {
                try {
                    return AdminDaoJpa.getInstance().find(id);
                } catch (DataBaseException e2) {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public Optional<? extends Person> find(String name) {
        try {
            return StudentDaoJpa.getInstance().find(name);
        } catch (DataBaseException e) {
            try {
                return TeacherDaoJpa.getInstance().find(name);
            } catch (DataBaseException e1) {
                try {
                    return AdminDaoJpa.getInstance().find(name);
                } catch (DataBaseException e2) {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public Person update(Integer id, Person person) {
        if (person instanceof Student) {
            return StudentDaoJpa.getInstance().update(id, (Student) person);
        }
        if (person instanceof Teacher) {
            return TeacherDaoJpa.getInstance().update(id, (Teacher) person);
        }
        if (person instanceof Admin) {
            return AdminDaoJpa.getInstance().update(id, (Admin) person);
        }
        return null;
    }

    @Override
    public Person remove(Person person) {
        if (person instanceof Student) {
            StudentDaoJpa.getInstance().remove((Student) person);
            return person;
        }
        if (person instanceof Teacher) {
            TeacherDaoJpa.getInstance().remove((Teacher) person);
            return person;
        }
        return AdminDaoJpa.getInstance().remove((Admin) person);
    }


    public List<? extends Person> findAll(Role role) {
        if (Role.STUDENT.equals(role)) {
            return StudentDAOPostgres.getInstance().findAll();
        } else if (Role.TEACHER.equals(role)) {
            return TeacherDAOPostgres.getInstance().findAll();
        }
        return new ArrayList<>();
    }

    @Override
    protected Class<Person> getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String findAllJpql() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String nameNamedQuery() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        PersonDaoJpa instance = PersonDaoJpa.getInstance();

        System.out.println(instance.remove(instance.find(1).orElseThrow()));
    }
}
