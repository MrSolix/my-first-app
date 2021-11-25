package by.dutov.jee.repository.person.postgres;


import by.dutov.jee.people.Admin;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.people.Role;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AdminDAOPostgres extends AbstractPersonDAOPostgres<Admin> {
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
    protected String selectUser() {
        return SELECT_ADMIN;
    }

    @Override
    protected String deleteUser() {
        return DELETE_ADMIN;
    }

    @Override
    protected String updateUser() {
        return UPDATE_ADMIN;
    }

    @Override
    protected String insertUser() {
        return INSERT_ADMIN;
    }

    @Override
    protected String selectUserById() {
        return SELECT_ADMIN_BY_ID;
    }

    @Override
    protected String selectUserByName() {
        return SELECT_ADMIN_BY_NAME;
    }

    @Override
    protected String deleteUserInGroup() {
        return null;
    }

    @Override
    protected List<Admin> resultSetToEntities(ResultSet rs) throws SQLException {
        Map<Integer, Admin> adminMap = new ConcurrentHashMap<>();
        while (rs.next()) {
            final int aId = rs.getInt(A_ID);
            final String aUserName = rs.getString(A_USER_NAME);
            final byte[] aPass = rs.getBytes(A_PASS);
            final byte[] aSalt = rs.getBytes(A_SALT);
            final String aName = rs.getString(A_NAME);
            final int aAge = rs.getInt(A_AGE);
            final Role role = Role.ADMIN;

            adminMap.putIfAbsent(aId, new Admin()
                    .withId(aId)
                    .withUserName(aUserName)
                    .withBytePass(aPass)
                    .withSalt(aSalt)
                    .withName(aName)
                    .withAge(aAge)
                    .withRole(role));

        }
        Collection<Admin> values = adminMap.values();
        return values.isEmpty() ? new ArrayList<>() : new ArrayList<>(values);
    }

    @Override
    protected List<Grade> getGrades(String name) {
        throw new UnsupportedOperationException();
    }
}
