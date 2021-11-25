package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.Optional;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

@Slf4j
public class StudentDaoJpa extends AbstractPersonDaoJpa<Student> {
    private static volatile StudentDaoJpa instance;

    public StudentDaoJpa() {
        //singleton
    }

    public static StudentDaoJpa getInstance() {
        if (instance == null) {
            synchronized (StudentDaoJpa.class) {
                if (instance == null) {
                    instance = new StudentDaoJpa();
                }
            }
        }
        return instance;
    }

    @Override
    public Student remove(Student student) {
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

    @Override
    protected Class<Student> getType() {
        return Student.class;
    }

    @Override
    protected String findAllJpql() {
        return "from Student";
    }

    @Override
    protected String nameNamedQuery() {
        return "findStudent";
    }
}
