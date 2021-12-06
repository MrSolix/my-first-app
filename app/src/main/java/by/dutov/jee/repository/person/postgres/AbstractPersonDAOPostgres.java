package by.dutov.jee.repository.person.postgres;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.repository.RepositoryDataSource;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.repository.group.postgres.GroupDAOPostgres;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.RepositoryDataSource.commitSingle;
import static by.dutov.jee.repository.RepositoryDataSource.connectionType;
import static by.dutov.jee.utils.DataBaseUtils.closeAndRemove;
import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

@Slf4j
public abstract class AbstractPersonDAOPostgres<T extends Person> implements PersonDAOInterface<T> {
    private static final int POSITION_ID = 1;

    @Override
    public T save(T t) {
        return t.getId() == null ? insert(t) : update(t.getId(), t);
    }

    @Override
    public Optional<? extends Person> find(String name) {
        List<? extends Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            con = RepositoryFactory.getDataSource().getConnection();
            ps = con.prepareStatement(selectUserByName());
            ps.setString(1, name);
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
            if (!result.isEmpty()) {
                commitSingle(con);
                return result.stream().findAny();
            }
            rollBack(con);
            throw new DataBaseException("Не удалось найти пользователя");
        } catch (SQLException e) {
            rollBack(con);
            log.error("Ошибка в методе поиска пользователя", e);
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps2, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                closeAndRemove(con);
            }
        }
    }

    @Override
    public Optional<? extends Person> find(Integer id) {
        List<? extends Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            con = RepositoryFactory.getDataSource().getConnection();
            ps = con.prepareStatement(selectUserById());
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
            if (!result.isEmpty()) {
                commitSingle(con);
                return result.stream().findAny();
            }
            rollBack(con);
            throw new DataBaseException("Не удалось найти пользователя");
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps2, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                closeAndRemove(con);
            }
        }
    }


    private void setterInsertOrUpdate(PreparedStatement ps, T t) throws SQLException {
        ps.setString(1, t.getUserName());
        ps.setBytes(2, t.getPassword());
        ps.setBytes(3, t.getSalt());
        ps.setString(4, t.getName());
        if (t.getAge() >= 0 || t.getAge() <= 99) {
            ps.setInt(5, t.getAge());
        } else {
            ps.setInt(5, 0);
        }
        ps.setString(6, Role.getStrByType(t.getRole()));
    }

    private T insert(T t) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = RepositoryFactory.getDataSource().getConnection();
            ps = con.prepareStatement(insertUser());
            setterInsertOrUpdate(ps, t);
            rs = ps.executeQuery();
            if (rs.next()) {
                commitSingle(con);
                return (T) t.withId(rs.getInt(POSITION_ID));
            }
            rollBack(con);
            log.error("Не удалось записать пользователя.");
            throw new DataBaseException("Не удалось записать пользователя.");
        } catch (SQLException e) {
            rollBack(con);
            log.error("Не удалось записать пользователя.", e);
            throw new DataBaseException("Не удалось записать пользователя.", e);
        } finally {
            closeQuietly(rs, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                closeAndRemove(con);
            }
        }
    }

    @Override
    public T update(Integer id, T t) {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        try {
            con = RepositoryFactory.getDataSource().getConnection();
            ps = con.prepareStatement(updateUser());
            setterInsertOrUpdate(ps, t);
            ps.setInt(7, id);
            if (ps.executeUpdate() > 0) {
                commitSingle(con);
                return t;
            }
            rollBack(con);
            log.error("Не удалось изменить пользователя.");
            throw new DataBaseException("Не удалось изменить пользователя.");
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps2, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                closeAndRemove(con);
            }
        }
    }

    @Override
    public T remove(T t) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = RepositoryFactory.getDataSource().getConnection();
            ps = con.prepareStatement(deleteUser());
            ps.setString(1, t.getUserName());
            if (ps.executeUpdate() <= 0) {
                rollBack(con);
                throw new DataBaseException("Не удалось удалить пользователя.");
            }
            commitSingle(con);
            return t;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                closeAndRemove(con);
            }
        }
    }

    @Override
    public List<? extends Person> findAll() {
        List<? extends Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = RepositoryFactory.getDataSource().getConnection();
            ps = con.prepareStatement(selectUser());
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
            if (!result.isEmpty()) {
                commitSingle(con);
                return result;
            }
            rollBack(con);
            return result;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                closeAndRemove(con);
            }
        }
    }

    protected abstract List<? extends Person> resultSetToEntities(ResultSet rs) throws SQLException;

    protected abstract String selectUser();

    protected abstract String deleteUser();

    protected abstract String updateUser();

    protected abstract String insertUser();

    protected abstract String selectUserById();

    protected abstract String selectUserByName();

}