package by.dutov.jee.repository.person.template;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.ConstantsClass;
import by.dutov.jee.repository.person.postgres.ConnectionType;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static by.dutov.jee.repository.AbstractGeneralTransaction.connectionType;
import static by.dutov.jee.repository.ConstantsClass.DELETE_TEACHER_FROM_GROUP;
import static by.dutov.jee.repository.ConstantsClass.G_ID;
import static by.dutov.jee.repository.ConstantsClass.SELECT_GROUP_BY_STUDENT_ID;
import static by.dutov.jee.repository.ConstantsClass.SELECT_GROUP_BY_TEACHER_ID;
import static by.dutov.jee.repository.ConstantsClass.SELECT_SALARY_BY_TEACHER_ID;
import static by.dutov.jee.repository.ConstantsClass.S_SALARY;
import static by.dutov.jee.repository.ConstantsClass.U_AGE;
import static by.dutov.jee.repository.ConstantsClass.U_ID;
import static by.dutov.jee.repository.ConstantsClass.U_NAME;
import static by.dutov.jee.repository.ConstantsClass.U_PASS;
import static by.dutov.jee.repository.ConstantsClass.U_SALT;
import static by.dutov.jee.repository.ConstantsClass.U_USER_NAME;

@Component
@Slf4j
public class TeacherDAOTemplate extends AbstractPersonDAOTemplate {

    public TeacherDAOTemplate() {
        this.rowMapper = (rs, rowNum) -> new Teacher()
                .withId(rs.getInt(U_ID))
                .withUserName(rs.getString(U_USER_NAME))
                .withBytePass(rs.getBytes(U_PASS))
                .withSalt(rs.getBytes(U_SALT))
                .withName(rs.getString(U_NAME))
                .withAge(rs.getInt(U_AGE));
    }

    @Override
    @Transactional
    public Optional<Person> find(String name) {
        return getUser(name, null);
    }

    @Override
    @Transactional
    public Optional<Person> find(Integer id) {
        return getUser(null, id);
    }

    @Override
    protected Optional<Person> getUser(String name, Integer id) {
        try {
            Optional<Person> person;
            if (name != null) {
                person = super.find(name);
            } else {
                person = super.find(id);
            }
            if (person.isPresent()) {
                Person user = person.get();
                Teacher teacher = (Teacher) user;
                teacher.setSalary(findSalary(teacher.getId()));
                Group group = getGroup(teacher.getId())
                        .stream().findFirst().orElse(null);
                teacher.setGroup(group);
                return Optional.of(teacher);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error from 'getUser' method in the 'TemplateTeacher' class", e);
            throw new DataBaseException(e);
        }
    }

    private double findSalary(Integer id) {
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_SALARY_BY_TEACHER_ID, id);
            if (rs.next()) {
                return rs.getDouble(S_SALARY);
            }
            return -1;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
    }

    @Override
    protected Set<Group> getGroup(Integer userId) {
        Map<Integer, Group> groupMap = new ConcurrentHashMap<>();
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_GROUP_BY_TEACHER_ID, userId);
            while (rs.next()) {
                int gId = rs.getInt(G_ID);
                groupMap.putIfAbsent(gId, new Group().withId(gId));
            }
        } catch (Exception e) {
            log.error("Error from 'getGroup' method in the 'TemplateTeacher' class");
            throw new DataBaseException(e);
        }
        Collection<Group> values = groupMap.values();
        return values.isEmpty() ? new HashSet<>() : new HashSet<>(values);
    }

    @Override
    @Transactional
    public Person save(Person person) {
        Teacher teacher = (Teacher) person;
        try {
            double lastSalary;
            double salary = getSalary(person);
            if (teacher.getSalary() != 0.0) {
                saveSalary(person, salary);
                lastSalary = teacher.getSalary();
            } else {
                lastSalary = salary;
            }
            if (person.getId() == null) {
                Person savedTeacher = super.save(person);
                ((Teacher) savedTeacher).setSalary(lastSalary);
                return savedTeacher;
            } else {
                Person updatedTeacher = super.update(person.getId(), person);
                ((Teacher) updatedTeacher).setSalary(lastSalary);
                return updatedTeacher;
            }
        } catch (Exception e) {
            log.error("Error from 'save' method in the 'TemplateTeacher' class", e);
            throw new DataBaseException("Error from 'save' method in the 'TemplateTeacher' class", e);
        }
    }

    private void saveSalary(Person person, double salary) {
        if (salary == -1) {
            jdbcTemplate.update("insert into salaries (teacher_id, salary) values (?, ?)", person.getId(), ((Teacher) person).getSalary());
        } else {
            jdbcTemplate.update("update salaries s set salary = ? where s.teacher_id = ?", ((Teacher) person).getSalary(), person.getId());
        }
    }

    private double getSalary(Person person) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(SELECT_SALARY_BY_TEACHER_ID, person.getId());
        double salary = -1;
        if (sqlRowSet.next()) {
            salary = sqlRowSet.getDouble(S_SALARY);
        }
        return salary;
    }

    @Override
    protected String setRole() {
        return Role.TEACHER.getType();
    }

    public Teacher updateTeacher(Teacher newTeacher, Teacher teacher) {
        Double salary = teacher.getSalary();
        Group group = teacher.getGroup();
        if (group != null) {
            saveGroup(newTeacher, teacher);
        }
        if (salary != null) {
            newTeacher.setSalary(salary);
            double oldSalary = getSalary(newTeacher);
            saveSalary(newTeacher, oldSalary);
        }
        return newTeacher;
    }

    private void saveGroup(Teacher newTeacher, Teacher teacher) {
        Optional<Group> optionalGroup = groupDaoTemplate.find(teacher.getGroup().getId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            if (group.getTeacher() == null) {
                Optional<Person> person = super.find(teacher.getId());
                person.ifPresent(value -> removeTeacherFromGroup(((Teacher) value)));
                group.setTeacher(teacher);
                groupDaoTemplate.save(group);
                newTeacher.setGroup(group);
            }
        }
    }

    private void removeTeacherFromGroup(Teacher teacher) {
        Set<Group> group = getGroup(teacher.getId());
        if (!group.isEmpty()) {
            int update = jdbcTemplate.update(DELETE_TEACHER_FROM_GROUP, teacher.getId());
            if (update <= 0) {
                throw new DataBaseException("Не удалось удалить учителя из группы");
            }
        }
    }
}
