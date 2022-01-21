package by.dutov.jee.repository.person.orm;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Teacher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;

import java.util.Optional;

import static by.dutov.jee.repository.ConstantsClass.REMOVE_TEACHER_FROM_GROUP;

@Repository
public class TeacherDaoSpringOrm extends AbstractPersonDaoSpringOrm {

    public TeacherDaoSpringOrm() {
        clazz = Teacher.class;
    }

    @Override
    public Person remove(Person person) {
        Teacher teacher = (Teacher) person;
        Group group = teacher.getGroup();
        if (group != null) {
            Query query = em.createQuery(REMOVE_TEACHER_FROM_GROUP);
            query.setParameter("id", group.getId());
            query.setParameter("teacher_id", teacher.getId());
            query.executeUpdate();
            teacher.removeGroup(group);
        }
        return super.remove(teacher);
    }

    public Teacher update(Teacher oldTeacher, Teacher teacher) {
        Group group = teacher.getGroup();
        Double salary = teacher.getSalary();
        if (group != null) {
            saveGroup(oldTeacher, teacher);
        }
        if (salary != null) {
            saveSalary(oldTeacher, teacher);
        }
        return em.merge(oldTeacher);
    }

    private void saveSalary(Teacher oldTeacher, Teacher teacher) {
        Double oldSalary = oldTeacher.getSalary();
        Double newSalary = teacher.getSalary();
        if (newSalary != null && !oldSalary.equals(newSalary)) {
            oldTeacher.setSalary(newSalary);
        }
    }

    private void saveGroup(Teacher oldTeacher, Teacher teacher) {
        Group newGroup = teacher.getGroup();
        Group oldGroup = oldTeacher.getGroup();
        Optional<Group> group = groupDaoSpringOrm.find(newGroup.getId());
        if (group.isPresent() && group.get().getTeacher() == null) {
            if (oldGroup != null) {
                oldGroup.setTeacher(null);
            }
            oldTeacher.setGroup(newGroup);
        }
    }

    @Override
    protected String findAllJpql() {
        return "from Teacher u where u.role = 'TEACHER'";
    }

    @Override
    protected String namedQueryByName() {
        return "findTeacherByName";
    }

    @Override
    protected String namedQueryById() {
        return "findTeacherById";
    }
}
