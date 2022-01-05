package by.dutov.jee.repository.group.postgres;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.RepositoryDataSource;
import by.dutov.jee.repository.group.GroupDAOInterface;
import by.dutov.jee.repository.person.postgres.ConnectionType;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

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

import static by.dutov.jee.repository.RepositoryDataSource.connectionType;

@Slf4j
@Repository("postgresGroup")
@Lazy
public class GroupDAOPostgres implements GroupDAOInterface {
    //language=SQL
    private static final String SELECT_ID_GROUP = "select g.id g_id from \"group\" g where g.id = ?;";
    //language=SQL
    private static final String SELECT_GROUP_ALL_FIELDS_FOR_STUDENT = "select " +
            "u.id u_id, u.user_name u_user_name, u.password u_pass, " +
            "u.salt u_salt, u.name u_name, u.age u_age, u.roles u_role " +
            "from users u " +
            "left join group_student gs " +
            "on u.id = gs.student_id " +
            "left join \"group\" g " +
            "on g.id = gs.group_id ";
    //language=SQL
    private static final String SELECT_GROUP_ALL_FIELDS_FOR_TEACHER = "select " +
            "u.id u_id, u.user_name u_user_name, u.password u_pass, " +
            "u.salt u_salt, u.name u_name, u.age u_age, u.roles u_role, t.salary t_salary " +
            "from users u " +
            "left join \"group\" g " +
            "on u.id = g.teacher_id " +
            "left join salaries t " +
            "on t.teacher_id = u.id";
    //language=SQL
    private static final String INSERT_GROUP = "insert into \"group\" (teacher_id) " +
            "values (?) returning id;";
    //language=SQL
    private static final String INSERT_STUDENT_IN_GROUP = "insert into group_student " +
            "(group_id, student_id) values (?, ?);";
    //language=SQL
    private static final String WHERE_ID = " where g.id = ?;";
    //language=SQL
    private static final String WHERE_GROUP_ID = " where gs.group_id = ? ";
    //language=SQL
    private static final String UPDATE_GROUP = "update \"group\" g set teacher_id = ?" + WHERE_ID;
    //language=SQL
    private static final String UPDATE_STUDENT_IN_GROUP = "update group_student gs set group_id = ?, student_id = ?" + WHERE_GROUP_ID +
            "and gs.student_id = ?;";
    //language=SQL
    private static final String DELETE_GROUP = "delete from \"group\" g " + WHERE_ID;
    //language=SQL
    private static final String DELETE_STUDENT_IN_GROUP = "delete from group_student gs where gs.group_id isnull";
    //language=SQL
    private static final String UPDATE_STUDENT_IN_GROUP_FOR_DELETE = "update group_student gs " +
            "set group_id = null, student_id = null" + WHERE_GROUP_ID + ";";
    //language=SQL
    private static final String SELECT_GROUP_FOR_TEACHER = SELECT_GROUP_ALL_FIELDS_FOR_TEACHER + WHERE_ID;
    private static final String SELECT_GROUP_FOR_STUDENT = SELECT_GROUP_ALL_FIELDS_FOR_STUDENT + WHERE_ID;
    public static final String G_ID = "g_id";
    public static final String U_ID = "u_id";
    public static final String U_USER_NAME = "u_user_name";
    public static final String U_PASS = "u_pass";
    public static final String U_SALT = "u_salt";
    public static final String U_NAME = "u_name";
    public static final String U_AGE = "u_age";
    public static final String U_ROLE = "u_role";
    public static final String T_SALARY = "t_salary";
    private static final int POSITION_ID = 1;


    private final RepositoryDataSource repositoryDataSource;

    @Autowired
    private GroupDAOPostgres(RepositoryDataSource repositoryDataSource) {
        this.repositoryDataSource = repositoryDataSource;
    }

    @Override
    public Group save(Group group) {
        return group.getId() == null ? insert(group) : update(group.getId(), group);
    }

    public boolean saveStudentInGroup(Group group, Student student) {
        Optional<Group> optionalGroup = find(group.getId());
        if (optionalGroup.isPresent()) {
            Group newGroup = optionalGroup.get();
            if (!newGroup.getStudents().contains(student)) {
                return insertStudentInGroup(newGroup, student);
            }
        }
        return false;
    }

    private Group insert(Group group) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(INSERT_GROUP);
            if (group.getTeacher() != null) {
                ps.setInt(1, group.getTeacher().getId());
            } else {
                ps.setObject(1, null);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                repositoryDataSource.commitSingle(con);
                return group.withId(rs.getInt(POSITION_ID));
            }
            repositoryDataSource.rollBack(con);
            throw new DataBaseException("Не удалось записать группу в базу");
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.closeQuietly(con);
            }
        }
    }

    private boolean insertStudentInGroup(Group group, Student student) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(INSERT_STUDENT_IN_GROUP);
            ps.setInt(1, group.getId());
            ps.setInt(2, student.getId());
            if (ps.executeUpdate() > 0) {
                repositoryDataSource.commitSingle(con);
                return true;
            }
            repositoryDataSource.rollBack(con);
            throw new DataBaseException("Не удалось записать студента в группу");
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.closeQuietly(con);
            }
        }
    }

    @Override
    public Optional<Group> find(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(SELECT_ID_GROUP);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            List<Group> groups = resultSetToGroup(rs);
            if (!(groups.isEmpty())) {
                repositoryDataSource.commitSingle(con);
                return groups.stream().findAny();
            }
            repositoryDataSource.rollBack(con);
            return Optional.empty();
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.closeQuietly(con);
            }
        }
    }

    @Override
    public Group update(Integer id, Group group) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(UPDATE_GROUP);
            ps.setInt(1, group.getTeacher().getId());
            ps.setInt(2, id);
            if (ps.executeUpdate() > 0) {
                repositoryDataSource.commitSingle(con);
                return group;
            }
            repositoryDataSource.rollBack(con);
            throw new DataBaseException("Не удалось изменить группу");
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.closeQuietly(con);
            }
        }
    }

    @Override
    public Group remove(Group group) {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        try {
            con = repositoryDataSource.getConnection();
            ps1 = con.prepareStatement(UPDATE_STUDENT_IN_GROUP_FOR_DELETE);
            ps2 = con.prepareStatement(DELETE_STUDENT_IN_GROUP);
            ps3 = con.prepareStatement(DELETE_GROUP);
            ps1.setInt(1, group.getId());
            ps3.setInt(1, group.getId());
            if (!(ps1.executeUpdate() > 0)
                    || !(ps2.executeUpdate() > 0)
                    || !(ps3.executeUpdate() > 0)) {
                repositoryDataSource.rollBack(con);
                throw new DataBaseException("Не удалось удалить группу");
            }
            repositoryDataSource.commitSingle(con);
            return group;
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(ps3, ps2, ps1);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.closeQuietly(con);
            }
        }
    }

    @Override
    public List<Group> findAll() {
        List<Group> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(SELECT_GROUP_ALL_FIELDS_FOR_TEACHER);
            rs = ps.executeQuery();
            result = resultSetToGroup(rs);
            repositoryDataSource.commitSingle(con);
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.closeQuietly(con);
            }
        }
        return result;
    }

    private List<Group> resultSetToGroup(ResultSet rs) throws SQLException {
        Map<Integer, Group> groupMap = new ConcurrentHashMap<>();
        while (rs.next()) {
            final int gId = rs.getInt(G_ID);
            Teacher teacher = getTeacherForGroup(gId);
            Set<Student> students = getStudentsForGroup(gId);
            groupMap.putIfAbsent(gId, new Group()
                    .withId(gId)
                    .withTeacher(teacher)
                    .withStudents(students));
        }
        Collection<Group> values = groupMap.values();
        return values.isEmpty() ? new ArrayList<>() : new ArrayList<>(values);
    }

    private Set<Student> getStudentsForGroup(int gId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Set<Student> students = new HashSet<>();
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(SELECT_GROUP_FOR_STUDENT);
            ps.setInt(1, gId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Student student = new Student()
                        .withId(rs.getInt(U_ID))
                        .withUserName(rs.getString(U_USER_NAME))
                        .withBytePass(rs.getBytes(U_PASS))
                        .withSalt(rs.getBytes(U_SALT))
                        .withName(rs.getString(U_NAME))
                        .withAge(rs.getInt(U_AGE))
                        .withRole(Role.getTypeByStr(rs.getString(U_ROLE)));
                student.addGroup(new Group().withId(gId));
                students.add(student);
            }
            return students;
        } catch (SQLException e) {
            log.error("Ошибка поиска студентов для группы", e);
            throw new DataBaseException("Ошибка поиска студентов для группы", e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
            if (connectionType.equals(ConnectionType.SINGLE)) {
                repositoryDataSource.closeQuietly(con);
            }
        }
    }

    private Teacher getTeacherForGroup(int gId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(SELECT_GROUP_FOR_TEACHER);
            ps.setInt(1, gId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Teacher()
                        .withId(rs.getInt(U_ID))
                        .withUserName(rs.getString(U_USER_NAME))
                        .withBytePass(rs.getBytes(U_PASS))
                        .withSalt(rs.getBytes(U_SALT))
                        .withName(rs.getString(U_NAME))
                        .withAge(rs.getInt(U_AGE))
                        .withRole(Role.getTypeByStr(rs.getString(U_ROLE)))
                        .withSalary(rs.getDouble(T_SALARY))
                        .withGroup(new Group()
                                .withId(gId)
                        );
            }
        } catch (SQLException e) {
            log.error("Ошибка поиска учителя для группы", e);
            throw new DataBaseException("Ошибка поиска учителя для группы", e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
            if (connectionType.equals(ConnectionType.SINGLE)) {
                repositoryDataSource.closeQuietly(con);
            }
        }
        return null;
    }
}
