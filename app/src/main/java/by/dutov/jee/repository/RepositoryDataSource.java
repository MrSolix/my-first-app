package by.dutov.jee.repository;

import by.dutov.jee.service.exceptions.ApplicationException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Slf4j
public class RepositoryDataSource implements DataSource {
    ComboPooledDataSource ds;

    private static volatile RepositoryDataSource instance;

    public RepositoryDataSource(String driver, String uri, String user, String password) {
        ds = new ComboPooledDataSource();
        try {
            ds.setDriverClass(driver);
        } catch (PropertyVetoException e) {
            log.info(e.getMessage());
            throw new ApplicationException("Ошибка с подключением драйвера в pool connection", e);
        }
        ds.setUser(user);
        ds.setPassword(password);
        ds.setJdbcUrl(uri);
        ds.setMinPoolSize(5);
        ds.setAcquireIncrement(5);
        ds.setMaxPoolSize(20);
    }

    public static RepositoryDataSource getInstance(String driver, String uri, String user, String password){
        if (instance == null){
            synchronized (RepositoryDataSource.class){
                if (instance == null){
                    instance = new RepositoryDataSource(driver, uri, user, password);
                }
            }
        }
        return instance;
    }


    @Override
    public Connection getConnection() throws SQLException {
        Connection con = ds.getConnection();
        con.setAutoCommit(false);
        return con;
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
