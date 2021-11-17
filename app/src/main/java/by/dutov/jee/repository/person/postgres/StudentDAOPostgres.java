package by.dutov.jee.repository.person.postgres;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Grades;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.repository.group.postgres.GroupDAOPostgres;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

@Slf4j
public class StudentDAOPostgres extends AbstractPersonDAOPostgres<Student> {
    //language=SQL
    public static final String SELECT_STUDENT =
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
    public static final String DELETE_STUDENT_IN_GROUP = "delete from group_student gs " +
            "where gs.student_id = (select id from student s" + WHERE_STUDENT_NAME + "); ";
    //language=SQL
    public static final String SELECT_STUDENT_BY_NAME = SELECT_STUDENT + WHERE_STUDENT_NAME;
    //language=SQL
    public static final String SELECT_STUDENT_BY_ID = SELECT_STUDENT + WHERE_STUDENT_ID;
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

    public StudentDAOPostgres() {
        //singleton
    }

    public static StudentDAOPostgres getInstance() {
        if (instance == null) {
            synchronized (StudentDAOPostgres.class) {
                if (instance == null) {
                    instance = new StudentDAOPostgres();
                }
            }
        }
        return instance;
    }

    @Override
    protected String selectUser() {
        return SELECT_STUDENT;
    }

    @Override
    protected String deleteUser() {
        return DELETE_STUDENT;
    }

    @Override
    protected String updateUser() {
        return UPDATE_STUDENT;
    }

    @Override
    protected String insertUser() {
        return INSERT_STUDENT;
    }

    @Override
    protected String selectUserById() {
        return SELECT_STUDENT_BY_ID;
    }

    @Override
    protected String selectUserByName() {
        return SELECT_STUDENT_BY_NAME;
    }

    @Override
    protected String deleteUserInGroup() {
        return DELETE_STUDENT_IN_GROUP;
    }

    @Override
    public List<Grades> getGrades(String name) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = RepositoryFactory.getDataSource().getConnection();
            ps = con.prepareStatement(SELECT_GRADES + WHERE_STUDENT_NAME);
            ps.setString(1, name);
            rs = ps.executeQuery();
            List<Grades> grades = new ArrayList<>();
            while (rs.next()) {
                String tName = rs.getString(T_NAME);
                int gGrade = rs.getInt(G_GRADE);

                grades.add(new Grades()
                        .withName(tName)
                        .withGrades(gGrade)
                );
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

    @Override
    protected List<Student> resultSetToEntities(ResultSet rs) throws SQLException {
        GroupDAOPostgres instance = GroupDAOPostgres.getInstance(RepositoryFactory.getDataSource());
        Map<Integer, Student> studentMap = new ConcurrentHashMap<>();
        Map<Integer, Group> groupMap = new ConcurrentHashMap<>();
        while (rs.next()) {
            final int gId = rs.getInt(G_ID);
            final int sId = rs.getInt(S_ID);
            final String sUserName = rs.getString(S_USER_NAME);
            final byte[] sPass = rs.getBytes(S_PASS);
            final byte[] sSalt = rs.getBytes(S_SALT);
            final String sName = rs.getString(S_NAME);
            final int sAge = rs.getInt(S_AGE);
            final Role role = Role.STUDENT;

            studentMap.putIfAbsent(sId, new Student()
                    .withId(sId)
                    .withUserName(sUserName)
                    .withBytePass(sPass)
                    .withSalt(sSalt)
                    .withName(sName)
                    .withAge(sAge)
                    .withRole(role)
                    .withGrades(getGrades(sUserName))
                    .addGroup(putIfAbsentAndReturn(groupMap, gId,
                            instance.find(gId).orElse(new Group()))));

            studentMap.computeIfPresent(sId, (id, student) -> student.addGroup(groupMap.get(gId)));
        }
        Collection<Student> values = studentMap.values();
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
