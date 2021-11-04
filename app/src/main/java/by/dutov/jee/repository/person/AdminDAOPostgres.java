package by.dutov.jee.repository.person;


import by.dutov.jee.exceptions.DataBaseException;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static by.dutov.jee.utils.CloseClass.closeQuietly;

@Slf4j
public class AdminDAOPostgres implements PersonDAO<Admin> {
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
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?" + WHERE_ADMIN_NAME;
    //language=SQL
    private static final String DELETE_ADMIN = "delete from admin a" + WHERE_ADMIN_NAME;
    //language=SQL
    private static final String SELECT_ADMIN_BY_NAME = SELECT_ADMIN + WHERE_ADMIN_NAME;
    private static final String SELECT_ADMIN_BY_ID = SELECT_ADMIN + WHERE_ADMIN_ID;
    private static final int POSITION_ID = 1;
    public static final String A_ID = "a_id";
    public static final String A_NAME = "a_name";
    public static final String A_AGE = "a_age";
    public static final String A_USER_NAME = "a_user_name";
    public static final String A_PASS = "a_pass";
    public static final String A_SALT = "a_salt";

    private static volatile AdminDAOPostgres instance;
    private final DataSource dataSource;

    public AdminDAOPostgres(DataSource dataSource) {
        this.dataSource = dataSource;
        //singleton
    }

    public static AdminDAOPostgres getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (AdminDAOPostgres.class) {
                if (instance == null) {
                    instance = new AdminDAOPostgres(dataSource);
                }
            }
        }
        return instance;
    }

    @Override
    public Admin save(Admin admin) {
        return admin.getId() == null ? insert(admin) : update(admin.getUserName(), admin);
    }

    @Override
    public Optional<Admin> find(String name) {
        List<Admin> result;
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ADMIN_BY_NAME)) {
            ps.setString(1, name);
            rs = ps.executeQuery();
            result = resultSetToAdmins(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
        return result.stream().findAny();
    }

    @Override
    public Optional<Admin> find(Integer id) {
        List<Admin> result;
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ADMIN_BY_ID)) {
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToAdmins(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs);
        }
        return result.stream().findAny();
    }

    private Admin insert(Admin admin) {
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_ADMIN)) {
            ps.setString(1, admin.getUserName());
            ps.setBytes(2, admin.getPassword());
            ps.setBytes(3, admin.getSalt());
            ps.setString(4, admin.getName());
            ps.setInt(5, admin.getAge());
            rs = ps.executeQuery();
            if (rs.next()) {
                return admin.withId(rs.getInt(POSITION_ID));
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
    public Admin update(String name, Admin admin) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_ADMIN)) {
            ps.setString(1, admin.getUserName());
            ps.setBytes(2, admin.getPassword());
            ps.setBytes(3, admin.getSalt());
            ps.setString(4, admin.getName());
            ps.setInt(5, admin.getAge());
            ps.setString(6, name);
            if (ps.executeUpdate() > 0) {
                return admin;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException(e);
        }
    }

    @Override
    public Admin remove(Admin admin) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_ADMIN)) {
            return (Admin) PersonDAOPostgres.getInstance(dataSource).removePerson(admin, ps);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
    }

    @Override
    public List<Admin> findAll() {
        List<Admin> result;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ADMIN);
             ResultSet rs = ps.executeQuery()) {
            result = resultSetToAdmins(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
        return result;
    }

    private List<Admin> resultSetToAdmins(ResultSet rs) throws SQLException {
        Map<Integer, Admin> adminMap = new HashMap<>();
        while (rs.next()) {
            final int sId = rs.getInt(A_ID);


            adminMap.putIfAbsent(sId, new Admin()
                    .withId(sId)
                    .withName(rs.getString(A_NAME))
                    .withAge(rs.getInt(A_AGE))
                    .withUserName(rs.getString(A_USER_NAME))
                    .withBytePass(rs.getBytes(A_PASS))
                    .withSalt(rs.getBytes(A_SALT))
                    .withRole(Role.ADMIN));

        }
        Collection<Admin> values = adminMap.values();
        return values.isEmpty() ? new ArrayList<>() : new ArrayList<>(values);
    }
}
