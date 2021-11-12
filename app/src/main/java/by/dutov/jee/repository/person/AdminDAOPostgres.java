package by.dutov.jee.repository.person;


import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Role;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class AdminDAOPostgres extends PersonDAO<Admin> {
    //language=SQL
    private static final String SELECT_ADMIN = "select " +
            "a.id a_id, " +
            "a.user_name a_user_name, a.password a_pass, a.salt a_salt, " +
            "a.name a_name, a.age a_age " +
            "from admin a";
    //language=SQL
    private static final String WHERE_ADMIN_NAME = " where a.user_name = ?;";
    //language=SQL
    private static final String WHERE_ADMIN_ID = " where a.id = ?;";
    //language=SQL
    private static final String INSERT_ADMIN = "insert into admin (user_name, password, salt, \"name\", age)" +
            " values (?, ?, ?, ?, ?) returning id;";
    //language=SQL
    private static final String UPDATE_ADMIN = "update admin a " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?" + WHERE_ADMIN_ID;
    //language=SQL
    private static final String DELETE_ADMIN = "delete from admin a" + WHERE_ADMIN_NAME;
    //language=SQL
    private static final String SELECT_ADMIN_BY_NAME = SELECT_ADMIN + WHERE_ADMIN_NAME;
    private static final String SELECT_ADMIN_BY_ID = SELECT_ADMIN + WHERE_ADMIN_ID;
    public static final String A_ID = "a_id";
    public static final String A_NAME = "a_name";
    public static final String A_AGE = "a_age";
    public static final String A_USER_NAME = "a_user_name";
    public static final String A_PASS = "a_pass";
    public static final String A_SALT = "a_salt";

    private static volatile AdminDAOPostgres instance;

    public AdminDAOPostgres() {
        //singleton
    }

    public static AdminDAOPostgres getInstance() {
        if (instance == null) {
            synchronized (AdminDAOPostgres.class) {
                if (instance == null) {
                    instance = new AdminDAOPostgres();
                }
            }
        }
        return instance;
    }

    @Override
    String selectUser() {
        return SELECT_ADMIN;
    }

    @Override
    String deleteUser() {
        return DELETE_ADMIN;
    }

    @Override
    String updateUser() {
        return UPDATE_ADMIN;
    }

    @Override
    String insertUser() {
        return INSERT_ADMIN;
    }

    @Override
    String selectUserById() {
        return SELECT_ADMIN_BY_ID;
    }

    @Override
    String selectUserByName() {
        return SELECT_ADMIN_BY_NAME;
    }

    @Override
    String deleteUserInGroup() {
        return null;
    }

    @Override
    String[] aliases() {
        return new String[]{
                A_ID,
                A_USER_NAME,
                A_PASS,
                A_SALT,
                A_NAME,
                A_AGE,
                Role.getStrByType(Role.ADMIN)};
    }

    @Override
    Map<String, List<Integer>> getGrades(String name) {
        throw new UnsupportedOperationException();
    }
}
