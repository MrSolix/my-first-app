package by.dutov.jee.repository;

import by.dutov.jee.service.exceptions.ApplicationException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Slf4j
public class RepositoryDataSource implements DataSource {
    private final String driver;
    private final String uri;
    private final String user;
    private final String password;

    private static volatile RepositoryDataSource instance;

    public RepositoryDataSource(String driver, String uri, String user, String password) {
        this.driver = driver;
        this.uri = uri;
        this.user = user;
        this.password = password;
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new ApplicationException(e);
        }
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
        Connection con = DriverManager.getConnection(this.uri, this.user, this.password);
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
