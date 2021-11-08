package by.dutov.jee.repository.person;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.repository.group.GroupDAOPostgres;
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
public class StudentDAOPostgres extends PersonDAO<Student> {
    //language=SQL
    public static final String SELECT_FROM_STUDENT_ALL_FIELDS =
            "select " +
                    "s.id s_id, " +
                    "s.user_name s_user_name, s.password s_pass, s.salt s_salt, " +
                    "s.name s_name, s.age s_age, " +
                    "g.id g_id, t.id t_id " +
                    "from student s " +
                    "left join group_student gs " +
                    "on s.id = gs.student_id " +
                    "left join \"group\" g " +
                    "on g.id = gs.group_id " +
                    "left join teacher t " +
                    "on t.id = g.teacher_id";
    //language=SQL
    public static final String SELECT_GRADES = "select " +
            "g.grade g_grade, t.name t_name " +
            "from grades g " +
            "left join student s " +
            "on s.id = g.student_id " +
            "left join theme t " +
            "on t.id = g.theme_id";
    //language=SQL
    public static final String WHERE_STUDENT_NAME = " where s.user_name = ?";
    //language=SQL
    public static final String WHERE_STUDENT_ID = " where s.id = ?";
    //language=SQL
    public static final String INSERT_STUDENT = "insert into student (user_name, password, salt, \"name\", age)" +
            " values (?, ?, ?, ?, ?) returning id;";
    //language=SQL
    public static final String UPDATE_STUDENT = "update student s " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?" + WHERE_STUDENT_ID;
    //language=SQL
    public static final String DELETE_STUDENT = "delete from student s" + WHERE_STUDENT_NAME + ";";
    //language=SQL
    public static final String UPDATE_STUDENT_FOR_DELETE = "delete from group_student gs " +
            "where gs.student_id = (select id from student s " + WHERE_STUDENT_NAME + "); ";
    //language=SQL
    public static final String SELECT_STUDENT_BY_NAME = SELECT_FROM_STUDENT_ALL_FIELDS + WHERE_STUDENT_NAME;
    //language=SQL
    public static final String SELECT_STUDENT_BY_ID = SELECT_FROM_STUDENT_ALL_FIELDS + WHERE_STUDENT_ID;
    private static final int POSITION_ID = 1;
    public static final String S_USER_NAME = "s_user_name";
    public static final String S_PASS = "s_pass";
    public static final String S_SALT = "s_salt";
    public static final String S_ID = "s_id";
    public static final String G_ID = "g_id";
    public static final String S_NAME = "s_name";
    public static final String S_AGE = "s_age";
    public static final String G_GRADE = "g_grade";
    public static final String T_NAME = "t_name";

    private static volatile StudentDAOPostgres instance;
    private final DataSource dataSource;

    public StudentDAOPostgres(DataSource dataSource) {
        this.dataSource = dataSource;
        //singleton
    }

    public static StudentDAOPostgres getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (StudentDAOPostgres.class) {
                if (instance == null) {
                    instance = new StudentDAOPostgres(dataSource);
                }
            }
        }
        return instance;
    }

    @Override
    void sqlForFind(String sql) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
    }

    @Override
    public Student save(Student student) {
        return student.getId() == null ? insert(student) : update(student.getId(), student);
    }

    @Override
    public Optional<Student> find(String name) {
        List<Student> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_STUDENT_BY_NAME);
            ps.setString(1, name);
            rs = ps.executeQuery();
            result = resultSetToStudents(rs);
            if (!result.isEmpty()) {
                con.commit();
                return result.stream().findAny();
            }
            rollBack(con);
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> find(Integer id) {
        List<Student> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_STUDENT_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToStudents(rs);
            if (!result.isEmpty()) {
                con.commit();
                return result.stream().findAny();
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

    private Student insert(Student student) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(INSERT_STUDENT);
            ps.setString(1, student.getUserName());
            ps.setBytes(2, student.getPassword());
            ps.setBytes(3, student.getSalt());
            ps.setString(4, student.getName());
            ps.setInt(5, student.getAge());
            rs = ps.executeQuery();
            if (rs.next()) {
                con.commit();
                return student.withId(rs.getInt(POSITION_ID));
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

    @Override
    public Student update(Integer id, Student student) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(UPDATE_STUDENT);
            ps.setString(1, student.getUserName());
            ps.setBytes(2, student.getPassword());
            ps.setBytes(3, student.getSalt());
            ps.setString(4, student.getName());
            ps.setInt(5, student.getAge());
            ps.setInt(6, id);
            if (ps.executeUpdate() > 0) {
                con.commit();
                return student;
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

    @Override
    public Student remove(Student student) {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        try {
            con = dataSource.getConnection();
            ps1 = con.prepareStatement(UPDATE_STUDENT_FOR_DELETE);
            ps2 = con.prepareStatement(DELETE_STUDENT);
            ps1.setString(1, student.getUserName());
            ps2.setString(1, student.getUserName());
            if (!(ps1.executeUpdate() > 0) || !(ps2.executeUpdate() > 0)) {
                rollBack(con);
                return null;
            }
            con.commit();
            return student;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps2, ps1, con);
        }
    }

    @Override
    public List<Student> findAll() {
        List<Student> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_FROM_STUDENT_ALL_FIELDS);
            rs = ps.executeQuery();
            result = resultSetToStudents(rs);
            if (!result.isEmpty()) {
                con.commit();
                return result;
            }
            rollBack(con);
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return result;
    }

    @Override
    String[] aliases() {
        return new String[]{G_ID, S_ID, S_USER_NAME, S_PASS, S_SALT, S_NAME, S_AGE, Role.getStrByType(Role.STUDENT)};
    }

    @Override
    public List<Student> findAll(Role role) {
        throw new UnsupportedOperationException();
    }

    private List<Student> resultSetToStudents(ResultSet rs) throws SQLException {
        Map<Integer, Student> studentMap = new HashMap<>();
        Map<Integer, Group> groupMap = new HashMap<>();
        while (rs.next()) {
            final int sId = rs.getInt(S_ID);
            final int gId = rs.getInt(G_ID);

            studentMap.putIfAbsent(sId, new Student()
                    .withId(sId)
                    .withName(rs.getString(S_NAME))
                    .withAge(rs.getInt(S_AGE))
                    .withUserName(rs.getString(S_USER_NAME))
                    .withBytePass(rs.getBytes(S_PASS))
                    .withSalt(rs.getBytes(S_SALT))
                    .withRole(Role.STUDENT)
                    .withGrades(getGrades(rs.getString(S_USER_NAME)))
                    .addGroup(putIfAbsentAndReturn(groupMap, gId, GroupDAOPostgres.getInstance(dataSource).find(gId).orElse(null))));

            studentMap.computeIfPresent(sId, (id, student) -> student.addGroup(groupMap.get(gId)));
        }
        Collection<Student> values = studentMap.values();
        return values.isEmpty() ? new ArrayList<>() : new ArrayList<>(values);
    }

    public Map<String, List<Integer>> getGrades(String name) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_GRADES + WHERE_STUDENT_NAME);
            ps.setString(1, name);
            rs = ps.executeQuery();
            Map<String, List<Integer>> grades = new HashMap<>();
            while (rs.next()) {
                final String t_name = rs.getString(T_NAME);
                final int g_grade = rs.getInt(G_GRADE);
                grades.putIfAbsent(t_name, new ArrayList<>());
                grades.get(t_name).add(g_grade);
            }
            con.commit();
            return grades;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    private static <K, V> V putIfAbsentAndReturn(Map<K, V> map, K key, V value) {
        if (key == null) {
            return null;
        }
        map.putIfAbsent(key, value);
        return map.get(key);
    }
}
