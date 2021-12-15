package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.repository.EntityManagerHelper;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

@Slf4j
@Repository("jpaPerson")
@Lazy
public class PersonDaoJpa extends AbstractPersonDaoJpa<Person> {
    private Class<? extends Person> classType;
    private String findAllJpql;
    private String namedQueryByName;
    private String namedQueryById;

    @Autowired
    public PersonDaoJpa(EntityManagerHelper entityManagerHelper) {
        super(entityManagerHelper);
    }

    @Override
    public Optional<? extends Person> find(Integer id) {
        try {
            setParameters(Role.STUDENT);
            return super.find(id);
        } catch (DataBaseException e) {
            try {
                setParameters(Role.TEACHER);
                return super.find(id);
            } catch (DataBaseException e1) {
                try {
                    setParameters(Role.ADMIN);
                    return super.find(id);
                } catch (DataBaseException e2) {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public Optional<? extends Person> find(String name) {
        try {
            setParameters(Role.STUDENT);
            return super.find(name);
        } catch (DataBaseException e) {
            try {
                setParameters(Role.TEACHER);
                return super.find(name);
            } catch (DataBaseException e1) {
                try {
                    setParameters(Role.ADMIN);
                    return super.find(name);
                } catch (DataBaseException e2) {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public Person update(Integer id, Person person) {
        return super.update(id, person);
    }

    @Override
    public Person remove(Person person) {
        switch (person.getRole()) {
            case STUDENT:
                return removeStudent((Student) person);
            case TEACHER:
                return removeTeacher((Teacher) person);
        }
        throw new DataBaseException("Не удалось удалить пользователя");
    }

    private Student removeStudent(Student student) {
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            Student naturalStudent = em.find(Student.class, student.getId());
            for (int i = 0; i < naturalStudent.getGroups().size(); i++) {
                Optional<Group> first = naturalStudent.getGroups().stream().findFirst();
                first.ifPresent(naturalStudent::removeGroup);
            }
            for (int i = 0; i < naturalStudent.getGrades().size(); i++) {
                Grade grade = naturalStudent.getGrades().get(i);
                em.remove(grade);
            }

            em.remove(naturalStudent);

            em.getTransaction().commit();
            return naturalStudent;
        } catch (Exception e) {
            rollBack(em);
            log.error(ERROR_FROM_REMOVE);
            throw new DataBaseException(ERROR_FROM_REMOVE);
        } finally {
            closeQuietly(em);
        }
    }

    private Teacher removeTeacher(Teacher teacher) {
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            Teacher naturalTeacher = em.find(Teacher.class, teacher.getId());
            naturalTeacher.setGroup(null);

            em.remove(naturalTeacher);

            em.getTransaction().commit();
            return naturalTeacher;
        } catch (Exception e) {
            rollBack(em);
            log.error(ERROR_FROM_REMOVE);
            throw new DataBaseException(ERROR_FROM_REMOVE);
        } finally {
            closeQuietly(em);
        }
    }

    @Override
    public List<? extends Person> findAll() {
        setParameters(Role.STUDENT);
        List<Person> personList = new ArrayList<>(super.findAll());
        setParameters(Role.TEACHER);
        personList.addAll(super.findAll());
        return personList;
    }

    @Override
    protected Class<? extends Person> getType() {
        return classType;
    }

    @Override
    protected String findAllJpql() {
        return findAllJpql;
    }

    @Override
    protected String namedQueryByName() {
        return namedQueryByName;
    }

    @Override
    protected String namedQueryById() {
        return namedQueryById;
    }

    private void setParameters(Role role) {
        switch (role) {
            case STUDENT:
                classType = Student.class;
                findAllJpql = "from Student u where u.role = 'STUDENT'";
                namedQueryByName = "findStudentByName";
                namedQueryById = "findStudentById";
                return;
            case TEACHER:
                classType = Teacher.class;
                findAllJpql = "from Teacher u where u.role = 'TEACHER'";
                namedQueryByName = "findTeacherByName";
                namedQueryById = "findTeacherById";
                return;
            case ADMIN:
                classType = Admin.class;
                findAllJpql = "from Admin u where u.role = 'ADMIN'";
                namedQueryByName = "findAdminByName";
                namedQueryById = "findAdminById";
        }
    }
}
