package by.dutov.jee.repository.person.postgres;

import by.dutov.jee.people.Admin;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PersonDAOPostgres extends AbstractPersonDAOPostgres<Person> {
    private static volatile PersonDAOPostgres instance;

    public PersonDAOPostgres() {
        //singleton
    }

    public static PersonDAOPostgres getInstance() {
        if (instance == null) {
            synchronized (PersonDAOPostgres.class) {
                if (instance == null) {
                    instance = new PersonDAOPostgres();
                }
            }
        }
        return instance;
    }

    @Override
    public Person save(Person person) {
        if (person instanceof Student) {
            return StudentDAOPostgres.getInstance().save((Student) person);
        }
        if (person instanceof Teacher) {
            return TeacherDAOPostgres.getInstance().save((Teacher) person);
        }
        if (person instanceof Admin) {
            return AdminDAOPostgres.getInstance().save((Admin) person);
        }
        return null;
    }

    @Override
    public Optional<? extends Person> find(String name) {
        Optional<? extends Person> student = StudentDAOPostgres.getInstance().find(name);
        if (student.isPresent()) {
            return student;
        }
        Optional<? extends Person> teacher = TeacherDAOPostgres.getInstance().find(name);
        if (teacher.isPresent()) {
            return teacher;
        }
        return AdminDAOPostgres.getInstance().find(name);
    }

    @Override
    public Optional<? extends Person> find(Integer id) {
        Optional<? extends Person> student = StudentDAOPostgres.getInstance().find(id);
        if (student.isPresent()) {
            return student;
        }
        Optional<? extends Person> teacher = TeacherDAOPostgres.getInstance().find(id);
        if (teacher.isPresent()) {
            return teacher;
        }
        return AdminDAOPostgres.getInstance().find(id);
    }

    @Override
    public Person update(Integer id, Person person) {
        if (person instanceof Student) {
            return StudentDAOPostgres.getInstance().update(id, (Student) person);
        }
        if (person instanceof Teacher) {
            return TeacherDAOPostgres.getInstance().update(id, (Teacher) person);
        }
        if (person instanceof Admin) {
            return AdminDAOPostgres.getInstance().update(id, (Admin) person);
        }
        return null;
    }

    @Override
    public Person remove(Person person) {
        if (person instanceof Student) {
            StudentDAOPostgres.getInstance().remove((Student) person);
            return person;
        }
        if (person instanceof Teacher) {
            TeacherDAOPostgres.getInstance().remove((Teacher) person);
            return person;
        }
        return AdminDAOPostgres.getInstance().remove((Admin) person);
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
    public List<Person> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<? extends Person> resultSetToEntities(ResultSet rs) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String selectUser() {
        return null;
    }

    @Override
    protected String deleteUser() {
        return null;
    }

    @Override
    protected String updateUser() {
        return null;
    }

    @Override
    protected String insertUser() {
        return null;
    }

    @Override
    protected String selectUserById() {
        return null;
    }

    @Override
    protected String selectUserByName() {
        return null;
    }

    @Override
    protected String deleteUserInGroup() {
        return null;
    }

    @Override
    protected List<Grade> getGrades(String name) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        PersonDAOPostgres instance = PersonDAOPostgres.getInstance();
        Teacher student = new Teacher()
                .withUserName("teacher")
                .withPassword("123")
                .withName("Ychitel")
                .withAge(30)
                .withSalary(5000);
        System.out.println(instance.find(3));
    }
}
