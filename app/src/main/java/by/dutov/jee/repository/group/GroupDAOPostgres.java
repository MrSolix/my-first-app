package by.dutov.jee.repository.group;

import by.dutov.jee.exceptions.DataBaseException;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.repository.person.StudentDAOPostgres;
import by.dutov.jee.repository.person.TeacherDAOPostgres;
import by.dutov.jee.utils.CloseClass;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static by.dutov.jee.utils.CloseClass.*;

@Slf4j
public class GroupDAOPostgres implements GroupDAO<Group> {
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
    private static final String WHERE_GROUP_ID = " where gs.group_id = ?;";
    //language=SQL
    private static final String UPDATE_GROUP = "update \"group\" g set teacher_id = ?" + WHERE_ID;
    //language=SQL
    private static final String UPDATE_STUDENT_IN_GROUP = "update group_student gs set group_id = ?, student_id = ?" + WHERE_GROUP_ID;
    //language=SQL
    private static final String DELETE_GROUP = "delete from \"group\" g " + WHERE_ID;
    //language=SQL
    private static final String DELETE_STUDENT_IN_GROUP = "delete from group_student gs";
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
        return (group.getId() == null || student.getId() == null) ? insertStudentInGroup(group, student) : updateStudentInGroup(group, student);
    }

    private Group insert(Group group) {
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_GROUP)) {
            ps.setInt(1, group.getTeacher().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                return group.withId(rs.getInt(POSITION_ID));
            }
            return null;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
    }

    private boolean insertStudentInGroup(Group group, Student student) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_STUDENT_IN_GROUP)) {
            ps.setInt(1, group.getId());
            ps.setInt(2, student.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
    }

    @Override
    public Optional<Group> find(Integer id) {
        List<Group> result;
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GROUP)) {
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToGroup(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
        return result.stream().findAny();
    }

    @Override
    public Group update(Integer id, Group group) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_GROUP)) {
            ps.setInt(1, group.getTeacher().getId());
            ps.setInt(2, id);
            if (ps.executeUpdate() > 0) {
                return group;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e);
        }
    }

    public boolean updateStudentInGroup(Group group, Student student) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_STUDENT_IN_GROUP)) {
            ps.setInt(1, group.getId());
            ps.setInt(2, student.getId());
            ps.setInt(3, group.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e);
        }
    }

    @Override
    public Group remove(Group group) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_GROUP)) {
            ps.setInt(1, group.getId());
            removeStudentInGroup(group.getId());
            return group;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
    }

    public boolean removeStudentInGroup(Integer id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_STUDENT_IN_GROUP + WHERE_GROUP_ID)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
    }

    @Override
    public List<Group> findAll() {
        List<Group> result;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GROUP_ALL_FIELDS);
             ResultSet rs = ps.executeQuery()) {
            result = resultSetToGroup(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
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

    public static void main(String[] args) {
        GroupDAOPostgres groupDAOPostgres = GroupDAOPostgres.getInstance(RepositoryFactory.getDataSource());
//        Student student1 = new Student()
//                .withUserName("test")
//                .withPassword("test")
//                .withName("test")
//                .withAge(0)
//                .withRole(Role.STUDENT);
//        System.out.println(StudentRepositoryPostgres.getInstance(RepositoryFactory.getDataSource()).create(student1));
//        System.out.println(StudentRepositoryPostgres.getInstance(RepositoryFactory.getDataSource()).delete("test"));
//        System.out.println(RepositoryFactory.getDaoRepository().get("student"));
//        System.out.println(groupDAOPostgres.saveStudentInGroup(groupDAOPostgres.get(6), (Student) RepositoryFactory.getDaoRepository().get("test")));
//        groupDAOPostgres.set(6, (Student) PersonDAOPostgres.getInstance(RepositoryFactory.getDataSource()).get("test"));
//        System.out.println(groupDAOPostgres.get(4));
//        System.out.println(groupDAOPostgres.remove(9));
//        System.out.println(StudentRepositoryPostgres.getInstance(RepositoryFactory.getDataSource()).readGrades("student"));
        System.out.println(groupDAOPostgres.findAll());
    }
}
