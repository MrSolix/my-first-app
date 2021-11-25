package by.dutov.jee.repository.person.postgres;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.repository.group.postgres.GroupDAOPostgres;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TeacherDAOPostgres extends AbstractPersonDAOPostgres<Teacher> {
    //language=SQL
    public static final String SELECT_TEACHER = "select " +
            "u.id t_id, u.user_name t_user_name, " +
            "u.password t_pass, u.salt t_salt, " +
            "u.name t_name, u.age t_age, u.role t_role, " +
            "g.id g_id , s.salary, " +
            "from users u " +
            "left join \"group\" g " +
            "on u.id = g.teacher_id " +
            "left join salaries s " +
            "on u.id = s.teacher_id;";
    //language=SQL
    public static final String WHERE_TEACHER_NAME = " where u.user_name = ? ";
    public static final String WHERE_TEACHER_ID = " where u.id = ? ";
    //language=SQL
    public static final String UPDATE_TEACHER = "update users u " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?" + WHERE_TEACHER_ID;
    //language=SQL
    public static final String DELETE_TEACHER = "delete from users u" + WHERE_TEACHER_NAME + ";";
    //language=SQL
    public static final String DELETE_TEACHER_IN_GROUP = "update \"group\" g set teacher_id = null " +
            "where teacher_id = (select id from users u " + WHERE_TEACHER_NAME + "); ";
    //language=SQL
    public static final String INSERT_TEACHER = "insert into users (user_name, password, salt, \"name\", age, role)" +
            " values (?, ?, ?, ?, ?, ?) returning id;";
    //language=SQL
    public static final String INSERT_SALARY_FOR_TEACHER = "insert into salaries (salary)" +
            " values (?);";
    //language=SQL
    public static final String SELECT_TEACHER_BY_NAME = SELECT_TEACHER + WHERE_TEACHER_NAME;
    public static final String SELECT_TEACHER_BY_ID = SELECT_TEACHER + WHERE_TEACHER_ID;
    public static final String T_ID = "t_id";
    public static final String G_ID = "g_id";
    public static final String T_NAME = "t_name";
    public static final String T_AGE = "t_age";
    public static final String T_USER_NAME = "t_user_name";
    public static final String T_PASS = "t_pass";
    public static final String T_SALT = "t_salt";
    public static final String T_ROLE = "t_role";
    public static final String T_SALARY = "t_salary";


    private static volatile TeacherDAOPostgres instance;

    public TeacherDAOPostgres() {
        //singleton
    }

    public static TeacherDAOPostgres getInstance() {
        if (instance == null) {
            synchronized (TeacherDAOPostgres.class) {
                if (instance == null) {
                    instance = new TeacherDAOPostgres();
                }
            }
        }
        return instance;
    }

    @Override
    protected String selectUser() {
        return SELECT_TEACHER;
    }

    @Override
    protected String deleteUser() {
        return DELETE_TEACHER;
    }

    @Override
    protected String updateUser() {
        return UPDATE_TEACHER;
    }

    @Override
    protected String insertUser() {
        return INSERT_TEACHER;
    }

    @Override
    protected String selectUserById() {
        return SELECT_TEACHER_BY_ID;
    }

    @Override
    protected String selectUserByName() {
        return SELECT_TEACHER_BY_NAME;
    }

    @Override
    protected String deleteUserInGroup() {
        return DELETE_TEACHER_IN_GROUP;
    }

    @Override
    protected List<Teacher> resultSetToEntities(ResultSet rs) throws SQLException {
        GroupDAOPostgres instance = GroupDAOPostgres.getInstance(RepositoryFactory.getDataSource());
        Map<Integer, Teacher> teacherMap = new ConcurrentHashMap<>();
        Map<Integer, Group> groupMap = new ConcurrentHashMap<>();
        while (rs.next()) {
            final int gId = rs.getInt(G_ID);
            final int tId = rs.getInt(T_ID);
            final String tUserName = rs.getString(T_USER_NAME);
            final byte[] tPass = rs.getBytes(T_PASS);
            final byte[] tSalt = rs.getBytes(T_SALT);
            final String tName = rs.getString(T_NAME);
            final int tAge = rs.getInt(T_AGE);
            final Role role = Role.getTypeByStr(rs.getString(T_ROLE));
            final double tSalary = rs.getDouble(T_SALARY);

            teacherMap.putIfAbsent(tId, new Teacher()
                    .withId(tId)
                    .withUserName(tUserName)
                    .withBytePass(tPass)
                    .withSalt(tSalt)
                    .withName(tName)
                    .withAge(tAge)
                    .withRole(role)
                    .withSalary(tSalary)
                    .withGroup(putIfAbsentAndReturn(groupMap, gId,
                            instance.find(gId).orElse(new Group()))));

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

    @Override
    protected List<Grade> getGrades(String name) {
        throw new UnsupportedOperationException();
    }
}
