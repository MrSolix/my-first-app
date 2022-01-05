package by.dutov.jee.repository.person.postgres;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.repository.RepositoryDataSource;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.AbstractGeneralTransaction.connectionType;

@Slf4j
public abstract class AbstractPersonDAOPostgres implements PersonDAOInterface {
    private static final int POSITION_ID = 1;
    private final RepositoryDataSource repositoryDataSource;

    public AbstractPersonDAOPostgres(RepositoryDataSource repositoryDataSource) {
        this.repositoryDataSource = repositoryDataSource;
    }

    @Override
    public Person save(Person t) {
        return t.getId() == null ? insert(t) : update(t.getId(), t);
    }

    @Override
    public Optional<Person> find(String name) {
        List<Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(selectUserByName());
            ps.setString(1, name);
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
            if (!result.isEmpty()) {
                repositoryDataSource.commitSingle(con);
                return result.stream().findAny();
            }
            repositoryDataSource.rollBack(con);
            throw new DataBaseException("Не удалось найти пользователя");
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error("Ошибка в методе поиска пользователя", e);
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps2, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.close(con);
            }
        }
    }

    @Override
    public Optional<Person> find(Integer id) {
        List<Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(selectUserById());
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
            if (!result.isEmpty()) {
                repositoryDataSource.commitSingle(con);
                return result.stream().findAny();
            }
            repositoryDataSource.rollBack(con);
            throw new DataBaseException("Не удалось найти пользователя");
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps2, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.close(con);
            }
        }
    }


    private void setterInsertOrUpdate(PreparedStatement ps, Person t) throws SQLException {
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

    private Person insert(Person t) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(insertUser());
            setterInsertOrUpdate(ps, t);
            rs = ps.executeQuery();
            if (rs.next()) {
                repositoryDataSource.commitSingle(con);
                return (Person) t.withId(rs.getInt(POSITION_ID));
            }
            repositoryDataSource.rollBack(con);
            log.error("Не удалось записать пользователя.");
            throw new DataBaseException("Не удалось записать пользователя.");
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error("Не удалось записать пользователя.", e);
            throw new DataBaseException("Не удалось записать пользователя.", e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.close(con);
            }
        }
    }

    @Override
    public Person update(Integer id, Person t) {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(updateUser());
            setterInsertOrUpdate(ps, t);
            ps.setInt(7, id);
            if (ps.executeUpdate() > 0) {
                repositoryDataSource.commitSingle(con);
                return t;
            }
            repositoryDataSource.rollBack(con);
            log.error("Не удалось изменить пользователя.");
            throw new DataBaseException("Не удалось изменить пользователя.");
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(ps2, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.close(con);
            }
        }
    }

    @Override
    public Person remove(Person t) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(deleteUser());
            ps.setString(1, t.getUserName());
            if (ps.executeUpdate() <= 0) {
                repositoryDataSource.rollBack(con);
                throw new DataBaseException("Не удалось удалить пользователя.");
            }
            repositoryDataSource.commitSingle(con);
            return t;
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.close(con);
            }
        }
    }

    @Override
    public List<Person> findAll() {
        List<Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(selectUser());
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
            if (!result.isEmpty()) {
                repositoryDataSource.commitSingle(con);
                return result;
            }
            repositoryDataSource.rollBack(con);
            return result;
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
            if (ConnectionType.SINGLE.equals(connectionType)) {
                repositoryDataSource.close(con);
            }
        }
    }

    protected abstract List<Person> resultSetToEntities(ResultSet rs) throws SQLException;

    protected abstract String selectUser();

    protected abstract String deleteUser();

    protected abstract String updateUser();

    protected abstract String insertUser();

    protected abstract String selectUserById();

    protected abstract String selectUserByName();

}