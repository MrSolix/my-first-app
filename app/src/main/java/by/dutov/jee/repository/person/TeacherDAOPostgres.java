package by.dutov.jee.repository.person;

import by.dutov.jee.exceptions.DataBaseException;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.group.GroupDAOPostgres;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static by.dutov.jee.utils.CloseClass.closeQuietly;

@Slf4j
public class TeacherDAOPostgres implements PersonDAO<Teacher> {
    //language=SQL
    private static final String SELECT_TEACHER = "select " +
            "t.id t_id, t.user_name t_user_name, " +
            "t.password t_pass, t.salt t_salt, " +
            "t.name t_name, t.age t_age, " +
            "t.salary t_salary, g.id g_id " +
            "from teacher t " +
            "join \"group\" g " +
            "on t.id = g.teacher_id ";
    //language=SQL
    private static final String WHERE_TEACHER_NAME = " where t.user_name = ? ";
    private static final String WHERE_TEACHER_ID = " where t.id = ? ";
    //language=SQL
    private static final String UPDATE_TEACHER = "update teacher t " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?, salary = ?" + WHERE_TEACHER_NAME;
    private static final String DELETE_TEACHER = "update \"group\" g set teacher_id = null " +
            "where teacher_id = (select id from teacher t " + WHERE_TEACHER_NAME + "); " +
            "delete from teacher t" + WHERE_TEACHER_NAME + ";";
    //language=SQL
    private static final String INSERT_TEACHER = "insert into teacher (user_name, password, salt, \"name\", age, salary)" +
            " values (?, ?, ?, ?, ?, ?) returning id;";
    //language=SQL
    private static final String SELECT_TEACHER_BY_NAME = SELECT_TEACHER + WHERE_TEACHER_NAME;
    private static final String SELECT_TEACHER_BY_ID = SELECT_TEACHER + WHERE_TEACHER_ID;
    private static final int POSITION_ID = 1;
    public static final String T_ID = "t_id";
    public static final String G_ID = "g_id";
    public static final String T_NAME = "t_name";
    public static final String T_AGE = "t_age";
    public static final String T_USER_NAME = "t_user_name";
    public static final String T_PASS = "t_pass";
    public static final String T_SALT = "t_salt";
    public static final String T_SALARY = "t_salary";


    private static volatile TeacherDAOPostgres instance;
    private final DataSource dataSource;

    public TeacherDAOPostgres(DataSource dataSource) {
        this.dataSource = dataSource;
        //singleton
    }

    public static TeacherDAOPostgres getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (TeacherDAOPostgres.class) {
                if (instance == null) {
                    instance = new TeacherDAOPostgres(dataSource);
                }
            }
        }
        return instance;
    }

    @Override
    public Teacher save(Teacher teacher) {
        return teacher.getId() == null ? insert(teacher) : update(teacher.getUserName(), teacher);
    }

    private Teacher insert(Teacher teacher) {
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_TEACHER)) {
            ps.setString(1, teacher.getUserName());
            ps.setBytes(2, teacher.getPassword());
            ps.setBytes(3, teacher.getSalt());
            ps.setString(4, teacher.getName());
            ps.setInt(5, teacher.getAge());
            ps.setDouble(6, teacher.getSalary());
            rs = ps.executeQuery();
            if (rs.next()) {
                return teacher.withId(rs.getInt(POSITION_ID));
            }
            return null;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
    }

    @Override
    public Optional<Teacher> find(String name) {
        List<Teacher> result;
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_TEACHER_BY_NAME)) {
            ps.setString(1, name);
            rs = ps.executeQuery();
            result = resultSetToTeachers(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
        return result.stream().findAny();
    }

    @Override
    public Optional<Teacher> find(Integer id) {
        List<Teacher> result;
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_TEACHER_BY_ID)) {
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToTeachers(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
        return result.stream().findAny();
    }

    @Override
    public Teacher update(String name, Teacher teacher) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_TEACHER)) {
            ps.setString(1, teacher.getUserName());
            ps.setBytes(2, teacher.getPassword());
            ps.setBytes(3, teacher.getSalt());
            ps.setString(4, teacher.getName());
            ps.setInt(5, teacher.getAge());
            ps.setDouble(6, teacher.getSalary());
            ps.setString(7, name);
            if (ps.executeUpdate() > 0) {
                return teacher;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e);
        }
    }


    @Override
    public Teacher remove(Teacher teacher) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_TEACHER)) {
            ps.setString(1, teacher.getUserName());
            ps.setString(2, teacher.getUserName());
            if (ps.executeUpdate() > 0) {
                return teacher;
            }
            return null;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
    }

    @Override
    public List<Teacher> findAll(Role role) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> result;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_TEACHER);
             ResultSet rs = ps.executeQuery()) {
            result = resultSetToTeachers(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
        return result;
    }


    private List<Teacher> resultSetToTeachers(ResultSet rs) throws SQLException {
        Map<Integer, Teacher> teacherMap = new HashMap<>();
        Map<Integer, Group> groupMap = new HashMap<>();
        while (rs.next()) {
            final int tId = rs.getInt(T_ID);
            final int gId = rs.getInt(G_ID);

            teacherMap.putIfAbsent(tId, new Teacher()
                    .withId(tId)
                    .withName(rs.getString(T_NAME))
                    .withAge(rs.getInt(T_AGE))
                    .withUserName(rs.getString(T_USER_NAME))
                    .withBytePass(rs.getBytes(T_PASS))
                    .withSalt(rs.getBytes(T_SALT))
                    .withSalary(rs.getDouble(T_SALARY))
                    .withRole(Role.TEACHER)
                    .withGroup(putIfAbsentAndReturn(groupMap, gId, GroupDAOPostgres.getInstance(dataSource).find(gId).get())));

            teacherMap.computeIfPresent(tId, (id, teacher) -> teacher.withGroup(groupMap.get(gId)));
        }
        Collection<Teacher> values = teacherMap.values();
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
