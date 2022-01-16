package by.dutov.jee.repository.person.template;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.repository.ConstantsClass;
import by.dutov.jee.repository.group.postgres.GroupDAOPostgres;
import by.dutov.jee.repository.person.postgres.ConnectionType;
import by.dutov.jee.repository.person.postgres.PersonDAOPostgres;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static by.dutov.jee.repository.AbstractGeneralTransaction.connectionType;
import static by.dutov.jee.repository.ConstantsClass.G_GRADE;
import static by.dutov.jee.repository.ConstantsClass.G_ID;
import static by.dutov.jee.repository.ConstantsClass.G_THEME_NAME;
import static by.dutov.jee.repository.ConstantsClass.INSERT_GRADES_BY_STUDENT_ID;
import static by.dutov.jee.repository.ConstantsClass.INSERT_STUDENT_IN_GROUP;
import static by.dutov.jee.repository.ConstantsClass.SELECT_GRADES_BY_USERNAME;
import static by.dutov.jee.repository.ConstantsClass.SELECT_GROUP_BY_STUDENT_ID;
import static by.dutov.jee.repository.ConstantsClass.SELECT_GROUP_BY_TEACHER_ID;
import static by.dutov.jee.repository.ConstantsClass.UPDATE_GRADES_BY_STUDENT_ID;
import static by.dutov.jee.repository.ConstantsClass.U_AGE;
import static by.dutov.jee.repository.ConstantsClass.U_ID;
import static by.dutov.jee.repository.ConstantsClass.U_NAME;
import static by.dutov.jee.repository.ConstantsClass.U_PASS;
import static by.dutov.jee.repository.ConstantsClass.U_SALT;
import static by.dutov.jee.repository.ConstantsClass.U_USER_NAME;

@Component
@Slf4j
public class StudentDAOTemplate extends AbstractPersonDAOTemplate {

    private final PersonDAOPostgres personDAOPostgres;


    public StudentDAOTemplate(PersonDAOPostgres personDAOPostgres) {
        this.personDAOPostgres = personDAOPostgres;
        this.rowMapper = (rs, rowNum) -> new Student()
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
                Student student = (Student) user;
                student.setGrades(getGrades(user.getUserName()));
                Set<Group> groups = getGroup(student.getId());
                student.setGroups(groups);
                return Optional.of(student);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error from 'getUser' method in the 'TemplateStudent' class");
            throw new DataBaseException(e);
        }
    }

    private List<Grade> getGrades(String name) {
        List<Grade> grades = new ArrayList<>();
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_GRADES_BY_USERNAME, name);
            while (rs.next()) {
                int gId = rs.getInt(G_ID);
                String tName = rs.getString(G_THEME_NAME);
                int gGrade = rs.getInt(G_GRADE);


                grades.add(new Grade()
                        .withId(gId)
                        .withName(tName)
                        .withGrade(gGrade)
                );
            }
            return grades;
        } catch (Exception e) {
            log.error("Error from 'getGrades' method in the 'TemplateStudent' class");
            throw new DataBaseException(e);
        }
    }

    @Override
    protected Set<Group> getGroup(Integer userId) {
        Map<Integer, Group> groupMap = new ConcurrentHashMap<>();
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_GROUP_BY_STUDENT_ID, userId);
            while (rs.next()) {
                int gId = rs.getInt(G_ID);
                groupMap.putIfAbsent(gId, new Group().withId(gId));
            }
        } catch (Exception e) {
            log.error("Error from 'getGroup' method in the 'TemplateStudent' class");
            throw new DataBaseException(e);
        }
        Collection<Group> values = groupMap.values();
        return values.isEmpty() ? new HashSet<>() : new HashSet<>(values);
    }

    @Override
    protected String setRole() {
        return Role.STUDENT.getType();
    }

    public Student updateStudent(Student oldStudent, Student newStudent, Student student) {
        Set<Group> groups = student.getGroups();
        List<Grade> grades = student.getGrades();
        if (groups != null && !groups.isEmpty()) {
            newStudent.setGroups(groups);
            groups.forEach(group -> saveStudentInGroup(group, newStudent));
        }
        if (grades != null && !grades.isEmpty()) {
            List<Grade> newGrades = saveGrades(oldStudent, student);
            newStudent.setGrades(newGrades);
        }
        return newStudent;
    }

    public boolean saveStudentInGroup(Group group, Student student) {
        Optional<Group> optionalGroup = groupDaoTemplate.find(group.getId());
        if (optionalGroup.isPresent()) {
            Group newGroup = optionalGroup.get();
            if (!newGroup.getStudents().contains(student)) {
                return insertStudentInGroup(newGroup, student);
            }
        }
        return false;
    }

    private boolean insertStudentInGroup(Group group, Student student) {
        try {
            int update = jdbcTemplate.update(INSERT_STUDENT_IN_GROUP, group.getId(), student.getId());
            if (update > 0) {
                return true;
            }
            throw new DataBaseException("Не удалось записать студента в группу");
        } catch (Exception e) {
            log.error("Error from 'insertStudentInGroup' method in the 'TemplateStudent' class");
            throw new DataBaseException(e);
        }
    }

    private List<Grade> saveGrades(Student oldStudent, Student student) {
        List<Grade> grades = getGrades(oldStudent.getUserName());
        List<Grade> studentGrades = student.getGrades();
        Set<Grade> allGrades = new HashSet<>(grades);
        allGrades.removeAll(studentGrades);
        allGrades.addAll(studentGrades);
        if (allGrades.isEmpty()) {
            return new ArrayList<>();
        }
        List<Grade> updateGrades = personDAOPostgres.equalsGradeLists(grades, new ArrayList<>(allGrades));
        for (Grade g : updateGrades) {
            jdbcTemplate.update(UPDATE_GRADES_BY_STUDENT_ID, g.getThemeName(), g.getGrade(), student.getId(), g.getId());
        }
        List<Grade> newGrades = personDAOPostgres.checkNewGrades(allGrades, updateGrades);
        for (Grade g : newGrades) {
            jdbcTemplate.update(INSERT_GRADES_BY_STUDENT_ID, g.getThemeName(), g.getGrade(), student.getId());
        }
        List<Grade> result = new ArrayList<>();
        result.addAll(updateGrades);
        result.addAll(newGrades);
        return result;
    }
}
