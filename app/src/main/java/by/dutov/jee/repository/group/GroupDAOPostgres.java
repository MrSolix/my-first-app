package by.dutov.jee.repository.group;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

@Slf4j
public class GroupDAOPostgres extends GroupDAO<Group> {
    //language=SQL
    private static final String SELECT_GROUP_ALL_FIELDS = "select " +
            "g.id g_id, " +
            "t.id t_id, t.user_name t_user_name, t.password t_pass, " +
            "t.salt t_salt, t.name t_name, t.age t_age, t.salary t_salary, " +
            "s.id s_id, s.user_name s_user_name, s.password s_pass, " +
            "s.salt s_salt, s.name s_name, s.age s_age " +
            "from \"group\" g " +
            "left join teacher t " +
            "on t.id = g.teacher_id " +
            "left join group_student gs " +
            "on g.id = gs.group_id " +
            "left join student s " +
            "on s.id = gs.student_id";

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
    private static final String SELECT_GROUP = SELECT_GROUP_ALL_FIELDS + WHERE_ID;
    public static final String G_ID = "g_id";
    public static final String S_ID = "s_id";
    public static final String T_ID = "t_id";
    public static final String T_USER_NAME = "t_user_name";
    public static final String T_PASS = "t_pass";
    public static final String T_SALT = "t_salt";
    public static final String T_NAME = "t_name";
    public static final String T_AGE = "t_age";
    public static final String T_SALARY = "t_salary";
    public static final String S_USER_NAME = "s_user_name";
    public static final String S_PASS = "s_pass";
    public static final String S_SALT = "s_salt";
    public static final String S_NAME = "s_name";
    public static final String S_AGE = "s_age";
    private static final int POSITION_ID = 1;
    public static final String TH_NAME = "th_name";


    private static volatile GroupDAOPostgres instance;
    private final DataSource dataSource;

    public GroupDAOPostgres(DataSource dataSource) {
        this.dataSource = dataSource;
        //singleton
    }

    public static GroupDAOPostgres getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (GroupDAOPostgres.class) {
                if (instance == null) {
                    instance = new GroupDAOPostgres(dataSource);
                }
            }
        }
        return instance;
    }

    @Override
    public Group save(Group group) {
        return group.getId() == null ? insert(group) : update(group.getId(), group);
    }

    public boolean saveStudentInGroup(Group group, Student student) {
        return (find(group.getId()).isEmpty())
                ? insertStudentInGroup(group, student)
                : updateStudentInGroup(group, student);
    }

    private Group insert(Group group) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(INSERT_GROUP);
            ps.setInt(1, group.getTeacher().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                con.commit();
                return group.withId(rs.getInt(POSITION_ID));
            }
            rollBack(con);
            return null;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    private boolean insertStudentInGroup(Group group, Student student) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(INSERT_STUDENT_IN_GROUP);
            ps.setInt(1, group.getId());
            ps.setInt(2, student.getId());
            if (ps.executeUpdate() > 0) {
                con.commit();
                return true;
            }
            rollBack(con);
            return false;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps, con);
        }
    }

    @Override
    public Optional<Group> find(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_GROUP);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            List<Group> groups = resultSetToGroup(rs);
            if (!(groups.isEmpty())) {
                con.commit();
                return groups.stream().findAny();
            }
            rollBack(con);
            return Optional.empty();

        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public Group update(Integer id, Group group) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(UPDATE_GROUP);
            ps.setInt(1, group.getTeacher().getId());
            ps.setInt(2, id);
            if (ps.executeUpdate() > 0) {
                con.commit();
                return group;
            }
            rollBack(con);
            return null;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps, con);
        }
    }

    public boolean updateStudentInGroup(Group group, Student student) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(UPDATE_STUDENT_IN_GROUP);
            ps.setInt(1, group.getId());
            ps.setInt(2, student.getId());
            ps.setInt(3, group.getId());
            ps.setInt(4, student.getId());
            if (ps.executeUpdate() > 0) {
                con.commit();
                return true;
            }
            rollBack(con);
            return false;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps, con);
        }
    }

    @Override
    public Group remove(Group group) {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        try {
            con = dataSource.getConnection();
            ps1 = con.prepareStatement(UPDATE_STUDENT_IN_GROUP_FOR_DELETE);
            ps2 = con.prepareStatement(DELETE_STUDENT_IN_GROUP);
            ps3 = con.prepareStatement(DELETE_GROUP);
            ps1.setInt(1, group.getId());
            ps3.setInt(1, group.getId());
            if (!(ps1.executeUpdate() > 0)
                    || !(ps2.executeUpdate() > 0)
                    || !(ps3.executeUpdate() > 0)) {
                rollBack(con);
                return null;
            }
            con.commit();
            return group;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps3, ps2, ps1, con);
        }
    }

    @Override
    public List<Group> findAll() {
        List<Group> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_GROUP_ALL_FIELDS);
            rs = ps.executeQuery();
            result = resultSetToGroup(rs);
            con.commit();
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return result;
    }

    private List<Group> resultSetToGroup(ResultSet rs) throws SQLException {
        Map<Integer, Student> studentMap = new HashMap<>();
        Map<Integer, Teacher> teacherMap = new HashMap<>();
        Map<Integer, Group> groupMap = new HashMap<>();
        while (rs.next()) {
            final int gId = rs.getInt(G_ID);
            final int sId = rs.getInt(S_ID);
            final int tId = rs.getInt(T_ID);

            groupMap.putIfAbsent(gId, new Group()
                    .withId(gId)
                    .withTeacher(putIfAbsentAndReturn(teacherMap, tId, new Teacher()
                            .withId(tId)
                            .withUserName(rs.getString(T_USER_NAME))
                            .withBytePass(rs.getBytes(T_PASS))
                            .withSalt(rs.getBytes(T_SALT))
                            .withName(rs.getString(T_NAME))
                            .withAge(rs.getInt(T_AGE))
                            .withSalary(rs.getDouble(T_SALARY))
                            .withRole(Role.TEACHER)))
                    .addStudent(putIfAbsentAndReturn(studentMap, sId, new Student()
                            .withId(sId)
                            .withUserName(rs.getString(S_USER_NAME))
                            .withBytePass(rs.getBytes(S_PASS))
                            .withSalt(rs.getBytes(S_SALT))
                            .withName(rs.getString(S_NAME))
                            .withAge(rs.getInt(S_AGE))
                            .withRole(Role.STUDENT))));

            groupMap.computeIfPresent(gId, (id, group) -> group.addStudent(studentMap.get(sId)));

        }
        Collection<Group> values = groupMap.values();
        return values.isEmpty() ? new ArrayList<>() : new ArrayList<>(values);
    }

    private static <K, V> V putIfAbsentAndReturn(Map<K, V> map, K key, V value) {
        if (key == null) {
            return null;
        }
        map.putIfAbsent(key, value);
        return map.get(key);
    }
}
