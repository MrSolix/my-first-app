package by.dutov.jee.repository.group.template;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.ConstantsClass;
import by.dutov.jee.repository.group.GroupDAOInterface;
import by.dutov.jee.repository.person.postgres.ConnectionType;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static by.dutov.jee.repository.AbstractGeneralTransaction.connectionType;
import static by.dutov.jee.repository.ConstantsClass.DELETE_GROUP;
import static by.dutov.jee.repository.ConstantsClass.DELETE_STUDENT_IN_GROUP;
import static by.dutov.jee.repository.ConstantsClass.G_ID;
import static by.dutov.jee.repository.ConstantsClass.INSERT_GROUP;
import static by.dutov.jee.repository.ConstantsClass.SELECT_GROUP_ALL_FIELDS_FOR_STUDENT;
import static by.dutov.jee.repository.ConstantsClass.SELECT_GROUP_ALL_FIELDS_FOR_TEACHER;
import static by.dutov.jee.repository.ConstantsClass.SELECT_ID_GROUP;
import static by.dutov.jee.repository.ConstantsClass.SELECT_STUDENT_FOR_GROUP;
import static by.dutov.jee.repository.ConstantsClass.SELECT_TEACHER_FOR_GROUP;
import static by.dutov.jee.repository.ConstantsClass.T_SALARY;
import static by.dutov.jee.repository.ConstantsClass.UPDATE_GROUP;
import static by.dutov.jee.repository.ConstantsClass.UPDATE_STUDENT_IN_GROUP_FOR_DELETE;
import static by.dutov.jee.repository.ConstantsClass.U_AGE;
import static by.dutov.jee.repository.ConstantsClass.U_ID;
import static by.dutov.jee.repository.ConstantsClass.U_NAME;
import static by.dutov.jee.repository.ConstantsClass.U_PASS;
import static by.dutov.jee.repository.ConstantsClass.U_ROLE;
import static by.dutov.jee.repository.ConstantsClass.U_SALT;
import static by.dutov.jee.repository.ConstantsClass.U_USER_NAME;

@Component("templateGroup")
@RequiredArgsConstructor
@Slf4j
public class GroupDaoTemplate implements GroupDAOInterface {

    private final JdbcTemplate jdbcTemplate;
    private RowMapper<Group> rowMapper = (rs, rowNum) -> new Group().withId(rs.getInt(G_ID));

    @Override
    public Group save(Group group) {
        return group.getId() == null ? insert(group) : update(group.getId(), group);
    }

    private Group insert(Group group) {
        try {
            Integer id;
            if(group.getTeacher() != null) {
                id = jdbcTemplate.queryForObject(INSERT_GROUP, (rs, rowNum) ->
                                rs.getInt("id"),
                        group.getTeacher().getId()
                );
            } else {
                id = jdbcTemplate.queryForObject(INSERT_GROUP, (rs, rowNum) ->
                        rs.getInt("id"),
                        (Object) null
                );
            }
            return group.withId(id);
        } catch (Exception e) {
            log.error("Error from 'insert' method in the 'TemplateGroup' class");
            throw new DataBaseException(e);
        }
    }

    @Override
    @Transactional
    public Optional<Group> find(Integer id) {
        try {
            Group group = jdbcTemplate.queryForObject(SELECT_ID_GROUP, rowMapper, id);
            if (group != null) {
                Teacher teacher;
                try {
                    teacher = jdbcTemplate.queryForObject(SELECT_TEACHER_FOR_GROUP, (rs, rowNum) -> new Teacher()
                            .withId(rs.getInt(U_ID))
                            .withUserName(rs.getString(U_USER_NAME))
                            .withBytePass(rs.getBytes(U_PASS))
                            .withSalt(rs.getBytes(U_SALT))
                            .withName(rs.getString(U_NAME))
                            .withAge(rs.getInt(U_AGE))
                            .withRole(Role.getTypeByStr(rs.getString(U_ROLE)))
                            .withSalary(rs.getDouble(T_SALARY)), id
                    );
                } catch (EmptyResultDataAccessException empty) {
                    teacher = null;
                }
                List<Student> students = jdbcTemplate.query(SELECT_STUDENT_FOR_GROUP, (rs, rowNum) -> new Student()
                        .withId(rs.getInt(U_ID))
                        .withUserName(rs.getString(U_USER_NAME))
                        .withBytePass(rs.getBytes(U_PASS))
                        .withSalt(rs.getBytes(U_SALT))
                        .withName(rs.getString(U_NAME))
                        .withAge(rs.getInt(U_AGE))
                        .withRole(Role.getTypeByStr(rs.getString(U_ROLE))), id
                );
                students.forEach(student -> student.addGroup(group));
                if (teacher != null) {
                    teacher.setGroup(group);
                }
                group.setTeacher(teacher);
                group.setStudents(new HashSet<>(students));
                return Optional.of(group);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error from 'find' method in the 'TemplateGroup' class");
            throw new DataBaseException(e);
        }
    }

    @Override
    public Group update(Integer id, Group group) {
        try {
            int update = jdbcTemplate.update(UPDATE_GROUP, group.getTeacher().getId(), id);
            if (update > 0) {
                return group;
            }
        } catch (Exception e) {
            log.error("Error from 'update' method in the 'TemplateGroup' class");
            throw new DataBaseException(e);
        }
        throw new DataBaseException("Не удалось изменить группу");
    }

    @Override
    public Group remove(Group group) {
        try {
            int update1 = jdbcTemplate.update(UPDATE_STUDENT_IN_GROUP_FOR_DELETE, group.getId());
            int update2 = jdbcTemplate.update(DELETE_STUDENT_IN_GROUP);
            int update3 = jdbcTemplate.update(DELETE_GROUP, group.getId());
            if (update1 > 0 && update2 > 0 && update3 > 0) {
                return group;
            }
        } catch (Exception e) {
            log.error("Error from 'remove' method in the 'TemplateGroup' class");
            throw new DataBaseException(e);
        }
        throw new DataBaseException("Failed to delete group");
    }

    @Override
    public List<Group> findAll() {
        return new ArrayList<>();
    }
}
