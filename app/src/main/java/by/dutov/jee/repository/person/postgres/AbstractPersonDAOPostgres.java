package by.dutov.jee.repository.person.postgres;

import by.dutov.jee.people.Grades;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

@Slf4j
public abstract class AbstractPersonDAOPostgres<T extends Person> implements PersonDAOInterface<T> {
    private static final int POSITION_ID = 1;
    private final DataSource dataSource;
    private final GroupDAOPostgres instance;

    {
        dataSource = RepositoryFactory.getDataSource();
        instance = GroupDAOPostgres.getInstance(dataSource);
    }

    @Override
    public T save(T t) {
        return t.getId() == null ? insert(t) : update(t.getId(), t);
    }

    @Override
    public Optional<? extends Person> find(String name) {
        List<? extends Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(selectUserByName());
            ps.setString(1, name);
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
            if (!result.isEmpty()) {
                con.commit();
                return result.stream().findAny();
            }
            rollBack(con);
            return result.stream().findAny();
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public Optional<? extends Person> find(Integer id) {
        List<? extends Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(selectUserById());
            ps.setInt(1, id);
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
            if (!result.isEmpty()) {
                con.commit();
                return result.stream().findAny();
            }
            rollBack(con);
            return result.stream().findAny();
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
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
    }

    private T insert(T t) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(insertUser());
            setterInsertOrUpdate(ps, t);
            if (getClass().equals(TeacherDAOPostgres.class)) {
                ps.setDouble(6, ((Teacher) t).getSalary());
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                con.commit();
                return (T) t.withId(rs.getInt(POSITION_ID));
            }
            rollBack(con);
            throw new DataBaseException("Не удалось записать студента.");
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public T update(Integer id, T t) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(updateUser());
            setterInsertOrUpdate(ps, t);
            if (getClass().equals(TeacherDAOPostgres.class)) {
                ps.setDouble(6, ((Teacher) t).getSalary());
                ps.setInt(7, id);
            } else {
                ps.setInt(6, id);
            }
            if (ps.executeUpdate() > 0) {
                con.commit();
                return t;
            }
            rollBack(con);
            throw new DataBaseException("Не удалось изменить юзера.");
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps, con);
        }
    }

    @Override
    public T remove(T t) {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        try {
            con = dataSource.getConnection();
            if (getClass().equals(StudentDAOPostgres.class) || getClass().equals(TeacherDAOPostgres.class)) {
                ps1 = con.prepareStatement(deleteUserInGroup());
                ps1.setString(1, t.getUserName());
                if (getClass().equals(StudentDAOPostgres.class)) {
                    ps2 = con.prepareStatement("delete from grades gr " +
                            "where gr.student_id = (select id from student s where s.user_name = ?);");
                    ps2.setString(1, t.getUserName());
                }
            }
            ps3 = con.prepareStatement(deleteUser());
            ps3.setString(1, t.getUserName());
            if (ps1 != null)
                ps1.execute();
            if (ps2 != null)
                ps2.execute();
            if (ps3.executeUpdate() <= 0) {
                rollBack(con);
                throw new DataBaseException("Не удалось удалить юзера.");
            }
            con.commit();
            return t;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps3, ps2, ps1, con);
        }
    }

    public List<? extends Person> findAll(Role role) {
        return new ArrayList<>();
    }

    @Override
    public List<? extends Person> findAll() {
        List<? extends Person> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(selectUser());
            rs = ps.executeQuery();
            result = resultSetToEntities(rs);
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

    protected abstract List<? extends Person> resultSetToEntities(ResultSet rs) throws SQLException;

    protected abstract String selectUser();
    protected abstract String deleteUser();
    protected abstract String updateUser();
    protected abstract String insertUser();
    protected abstract String selectUserById();
    protected abstract String selectUserByName();
    protected abstract String deleteUserInGroup();


    protected abstract List<Grades> getGrades(String name);

}
