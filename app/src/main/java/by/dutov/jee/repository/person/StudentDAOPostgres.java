package by.dutov.jee.repository.person;

import by.dutov.jee.exceptions.DataBaseException;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
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
public class StudentDAOPostgres implements PersonDAO<Student> {
    //language=SQL
    private static final String SELECT_FROM_STUDENT_ALL_FIELDS =
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
    private static final String SELECT_GRADES = "select " +
            "g.grade g_grade, t.name t_name " +
            "from grades g " +
            "left join student s " +
            "on s.id = g.student_id " +
            "left join theme t " +
            "on t.id = g.theme_id";
    //language=SQL
    private static final String WHERE_STUDENT_NAME = " where s.user_name = ?;";
    //language=SQL
    private static final String WHERE_STUDENT_ID = " where s.id = ?;";
    //language=SQL
    private static final String INSERT_STUDENT = "insert into student (user_name, password, salt, \"name\", age)" +
            " values (?, ?, ?, ?, ?) returning id;";
    //language=SQL
    private static final String UPDATE_STUDENT = "update student s " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?" + WHERE_STUDENT_NAME;
    //language=SQL
    private static final String DELETE_STUDENT = "delete from student s" + WHERE_STUDENT_NAME;
    //language=SQL
    private static final String SELECT_STUDENT_BY_NAME = SELECT_FROM_STUDENT_ALL_FIELDS + WHERE_STUDENT_NAME;
    //language=SQL
    private static final String SELECT_STUDENT_BY_ID = SELECT_FROM_STUDENT_ALL_FIELDS + WHERE_STUDENT_ID;
    public static final String S_USER_NAME = "s_user_name";
    public static final String S_PASS = "s_pass";
    public static final String S_SALT = "s_salt";
    public static final String S_ID = "s_id";
    public static final String G_ID = "g_id";
    public static final String S_NAME = "s_name";
    public static final String S_AGE = "s_age";
    public static final String G_GRADE = "g_grade";
    public static final String T_NAME = "t_name";
    private static final int POSITION_ID = 1;

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
    public Student save(Student student) {
        return student.getId() == null ? insert(student) : update(student.getUserName(), student);
    }

    @Override
    public Optional<Student> find(String name) {
        List<Student> result;
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_STUDENT_BY_NAME)) {
            ps.setString(1, name);
            rs = ps.executeQuery();
            result = resultSetToStudents(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
        return result.stream().findAny();
    }

    @Override
    public Optional<Student> find(Integer id) {
        List<Student> result;
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_STUDENT_BY_ID)) {
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToStudents(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
        return result.stream().findAny();
    }

    private Student insert(Student student) {
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_STUDENT)) {
            ps.setString(1, student.getUserName());
            ps.setBytes(2, student.getPassword());
            ps.setBytes(3, student.getSalt());
            ps.setString(4, student.getName());
            ps.setInt(5, student.getAge());
            rs = ps.executeQuery();
            if (rs.next()) {
                return student.withId(rs.getInt(POSITION_ID));
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
    public Student update(String name, Student student) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_STUDENT)) {
            ps.setString(1, student.getUserName());
            ps.setBytes(2, student.getPassword());
            ps.setBytes(3, student.getSalt());
            ps.setString(4, student.getName());
            ps.setInt(5, student.getAge());
            ps.setString(6, name);
            if (ps.executeUpdate() > 0) {
                return student;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e);
        }
    }

    @Override
    public Student remove(Student student) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_STUDENT)) {
            return (Student) PersonDAOPostgres.getInstance(dataSource).removePerson(student, ps);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
    }

    @Override
    public List<Student> findAll() {
        List<Student> result;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_FROM_STUDENT_ALL_FIELDS);
             ResultSet rs = ps.executeQuery()) {
            result = resultSetToStudents(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
        return result;
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
                    .withGrades(getGrades(rs.getString(S_USER_NAME)))
                    .withUserName(rs.getString(S_USER_NAME))
                    .withBytePass(rs.getBytes(S_PASS))
                    .withSalt(rs.getBytes(S_SALT))
                    .withRole(Role.STUDENT)
                    .addGroup(putIfAbsentAndReturn(groupMap, gId, GroupDAOPostgres.getInstance(dataSource).find(gId).get())));

            studentMap.computeIfPresent(sId, (id, student) -> student.addGroup(groupMap.get(gId)));
        }
        Collection<Student> values = studentMap.values();
        return values.isEmpty() ? new ArrayList<>() : new ArrayList<>(values);
    }

    public Map<String, List<Integer>> getGrades(String name) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GRADES + WHERE_STUDENT_NAME)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            Map<String, List<Integer>> grades = new HashMap<>();
            while (rs.next()) {
                final String t_name = rs.getString(T_NAME);
                final int g_grade = rs.getInt(G_GRADE);
                grades.putIfAbsent(t_name, new ArrayList<>());
                grades.get(t_name).add(g_grade);
            }
            return grades;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
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
