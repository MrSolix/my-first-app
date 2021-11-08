package by.dutov.jee.repository.person;


import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Role;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

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
    void sqlForFind(String sql) {

    }

    @Override
    public Admin save(Admin admin) {
        return admin.getId() == null ? insert(admin) : update(admin.getId(), admin);
    }

    @Override
    public Optional<Admin> find(String name) {
        List<Admin> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_ADMIN_BY_NAME);
            ps.setString(1, name);
            rs = ps.executeQuery();
            result = resultSetToAdmins(rs);
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

    @Override
    public Optional<Admin> find(Integer id) {
        List<Admin> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_ADMIN_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToAdmins(rs);
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

    private Admin insert(Admin admin) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(INSERT_ADMIN);
            ps.setString(1, admin.getUserName());
            ps.setBytes(2, admin.getPassword());
            ps.setBytes(3, admin.getSalt());
            ps.setString(4, admin.getName());
            ps.setInt(5, admin.getAge());
            rs = ps.executeQuery();
            if (rs.next()) {
                con.commit();
                return admin.withId(rs.getInt(POSITION_ID));
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
    public Admin update(Integer id, Admin admin) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(UPDATE_ADMIN);
            ps.setString(1, admin.getUserName());
            ps.setBytes(2, admin.getPassword());
            ps.setBytes(3, admin.getSalt());
            ps.setString(4, admin.getName());
            ps.setInt(5, admin.getAge());
            ps.setInt(6, id);
            if (ps.executeUpdate() > 0) {
                con.commit();
                return admin;
            }
            rollBack(con);
            return null;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        }
    }

    @Override
    public Admin remove(Admin admin) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(DELETE_ADMIN);
            ps.setString(1, admin.getUserName());
            if (ps.executeUpdate() > 0) {
                con.commit();
                return admin;
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
    public List<Admin> findAll() {
        List<Admin> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_ADMIN);
            rs = ps.executeQuery();
            result = resultSetToAdmins(rs);
            if (!result.isEmpty()) {
                con.commit();
                return result;
            }
            rollBack(con);
            return result;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    String[] aliases() {
        return new String[0];
    }

    @Override
    public List<Admin> findAll(Role role) {
        throw new UnsupportedOperationException();
    }

    private List<Admin> resultSetToAdmins(ResultSet rs) throws SQLException {
        Map<Integer, Admin> adminMap = new ConcurrentHashMap<>();
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
