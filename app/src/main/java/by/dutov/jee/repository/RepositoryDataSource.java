package by.dutov.jee.repository;

import by.dutov.jee.repository.person.postgres.ConnectionType;
import by.dutov.jee.service.exceptions.ApplicationException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@NoArgsConstructor
@Slf4j
@Component
@PropertySource("classpath:app.properties")
public class RepositoryDataSource extends AbstractGeneralTransaction<Connection> implements DataSource {
    @Value("${postgres.driver}")
    private String driver;
    @Value("${postgres.uri}")
    private String uri;
    @Value("${postgres.user}")
    private String user;
    @Value("${postgres.password}")
    private String password;
    private static ComboPooledDataSource ds;

    @PostConstruct
    private void init() {
        ds = new ComboPooledDataSource();
        try {
            ds.setDriverClass(driver);
        } catch (PropertyVetoException e) {
            log.info(e.getMessage());
            throw new ApplicationException("Ошибка с подключением драйвера в pool connection", e);
        }
        ds.setJdbcUrl(uri);
        ds.setUser(user);
        ds.setPassword(password);
        ds.setMinPoolSize(5);
        ds.setAcquireIncrement(5);
        ds.setMaxPoolSize(20);
    }

    @Override
    public void commitSingle(Connection connection) throws SQLException {
        if (ConnectionType.SINGLE.equals(connectionType)) {
            connection.commit();
        }
    }

    public void commitMany(Connection connection) throws SQLException {
        connection.commit();
        connectionType = ConnectionType.SINGLE;
    }

    @Override
    public void rollBack(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
                log.error("Failed to rollback ", e);
            }
        }
    }

    @Override
    public Connection getConnection() {
        return getObject();
    }

    @Override
    @SneakyThrows
    public Connection getObject() {
        Connection connection = threadLocal.get();
        if (connection == null) {
            connection = ds.getConnection();
            threadLocal.set(connection);
        }
        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    public void remove() {
        if (threadLocal.get() != null) {
            threadLocal.remove();
        }
    }

    @Override
    public void close(Connection con) {
        try {
            if (con != null) {
                con.close();
                remove();
            }
        } catch (Exception e) {
            log.error("Couldn't close and remove connection", e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
