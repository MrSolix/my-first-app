package by.dutov.jee.repository.person;

import by.dutov.jee.people.*;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PersonDAOPostgres extends PersonDAO<Person> {
    private static volatile PersonDAOPostgres instance;
    private final StudentDAOPostgres studentInstance;
    private final TeacherDAOPostgres teacherInstance;
    private final AdminDAOPostgres adminInstance;

    public PersonDAOPostgres(DataSource dataSource) {
        this.studentInstance = StudentDAOPostgres.getInstance(dataSource);
        this.teacherInstance = TeacherDAOPostgres.getInstance(dataSource);
        this.adminInstance = AdminDAOPostgres.getInstance(dataSource);
        //singleton
    }

    public static PersonDAOPostgres getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (PersonDAOPostgres.class) {
                if (instance == null) {
                    instance = new PersonDAOPostgres(dataSource);
                }
            }
        }
        return instance;
    }

    @Override
    void sqlForFind(String sql) {

    }

    @Override
    public Person save(Person person) {
        if (person instanceof Student) {
            return studentInstance.save((Student) person);
        }
        if (person instanceof Teacher) {
            return teacherInstance.save((Teacher) person);
        }
        if (person instanceof Admin) {
            return adminInstance.save((Admin) person);
        }
        return null;
    }

    @Override
    public Optional<? extends Person> find(String name) {
        Optional<Student> student = studentInstance.find(name);
        if (student.isPresent()) {
            return student;
        }
        Optional<Teacher> teacher = teacherInstance.find(name);
        if (teacher.isPresent()) {
            return teacher;
        }
        return adminInstance.find(name);
    }

    @Override
    public Optional<? extends Person> find(Integer id) {
        Optional<Student> student = studentInstance.find(id);
        if (student.isPresent()) {
            return student;
        }
        Optional<Teacher> teacher = teacherInstance.find(id);
        if (teacher.isPresent()) {
            return teacher;
        }
        return adminInstance.find(id);
    }

    @Override
    public Person update(Integer id, Person person) {
        if (person instanceof Student) {
            return studentInstance.update(id, (Student) person);
        }
        if (person instanceof Teacher) {
            return teacherInstance.update(id, (Teacher) person);
        }
        if (person instanceof Admin) {
            return adminInstance.update(id, (Admin) person);
        }
        return null;
    }

    @Override
    public Person remove(Person person) {
        if (person instanceof Student) {
            studentInstance.remove((Student) person);
            return person;
        }
        if (person instanceof Teacher) {
            teacherInstance.remove((Teacher) person);
            return person;
        }
        return adminInstance.remove((Admin) person);
    }

    @Override
    public List<? extends Person> findAll(Role role) {
        if (Role.STUDENT.equals(role)) {
            return studentInstance.findAll();
        } else if (Role.TEACHER.equals(role)) {
            return teacherInstance.findAll();
        }
        return new ArrayList<>();
    }

    @Override
    public List<Person> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    String[] aliases() {
        return new String[0];
    }
}
