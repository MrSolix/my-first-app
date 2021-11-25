package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Optional;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

@Slf4j
public class TeacherDaoJpa extends AbstractPersonDaoJpa<Teacher> {
    private static volatile TeacherDaoJpa instance;

    public TeacherDaoJpa() {
        //singleton
    }

    public static TeacherDaoJpa getInstance() {
        if (instance == null) {
            synchronized (TeacherDaoJpa.class) {
                if (instance == null) {
                    instance = new TeacherDaoJpa();
                }
            }
        }
        return instance;
    }

    @Override
    public Teacher remove(Teacher teacher) {
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
    protected Class<Teacher> getType() {
        return Teacher.class;
    }

    @Override
    protected String findAllJpql() {
        return "from Teacher";
    }

    @Override
    protected String nameNamedQuery() {
        return "findTeacher";
    }
}
