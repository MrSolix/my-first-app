package by.dutov.jee.repository.person;

import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class TeacherDAOPostgres extends PersonDAO<Teacher> {
    //language=SQL
    public static final String SELECT_TEACHER = "select " +
            "t.id t_id, t.user_name t_user_name, " +
            "t.password t_pass, t.salt t_salt, " +
            "t.name t_name, t.age t_age, " +
            "t.salary t_salary, g.id g_id " +
            "from teacher t " +
            "left join \"group\" g " +
            "on t.id = g.teacher_id ";
    //language=SQL
    public static final String WHERE_TEACHER_NAME = " where t.user_name = ? ";
    public static final String WHERE_TEACHER_ID = " where t.id = ? ";
    //language=SQL
    public static final String UPDATE_TEACHER = "update teacher t " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?, salary = ?" + WHERE_TEACHER_ID;
    //language=SQL
    public static final String DELETE_TEACHER = "delete from teacher t" + WHERE_TEACHER_NAME + ";";
    //language=SQL
    public static final String DELETE_TEACHER_IN_GROUP = "update \"group\" g set teacher_id = null " +
            "where teacher_id = (select id from teacher t " + WHERE_TEACHER_NAME + "); ";
    //language=SQL
    public static final String INSERT_TEACHER = "insert into teacher (user_name, password, salt, \"name\", age, salary)" +
            " values (?, ?, ?, ?, ?, ?) returning id;";
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
    String selectUser() {
        return SELECT_TEACHER;
    }

    @Override
    String deleteUser() {
        return DELETE_TEACHER;
    }

    @Override
    String updateUser() {
        return UPDATE_TEACHER;
    }

    @Override
    String insertUser() {
        return INSERT_TEACHER;
    }

    @Override
    String selectUserById() {
        return SELECT_TEACHER_BY_ID;
    }

    @Override
    String selectUserByName() {
        return SELECT_TEACHER_BY_NAME;
    }

    @Override
    String deleteUserInGroup() {
        return DELETE_TEACHER_IN_GROUP;
    }

    @Override
    String[] aliases() {
        return new String[]{
                T_ID,
                T_USER_NAME,
                T_PASS,
                T_SALT,
                T_NAME,
                T_AGE,
                Role.getStrByType(Role.TEACHER),
                G_ID,
                T_SALARY};
    }

    @Override
    Map<String, List<Integer>> getGrades(String name) {
        throw new UnsupportedOperationException();
    }
}
