package by.dutov.jee.utils;

import by.dutov.jee.MyAppContext;
import by.dutov.jee.repository.RepositoryDataSource;
import by.dutov.jee.repository.RepositoryFactory;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class DataBaseUtils {

    public static void closeAndRemove(Connection con) {
        try {
            if (con != null) {
                con.close();
                MyAppContext.getContext().getBean(RepositoryDataSource.class).removeConnection();
            }
        } catch (Exception e) {
            log.error("Couldn't close and remove connection", e);
        }
    }

    public static void closeQuietly(EntityManager em) {
        try {
            if (em != null) {
                em.close();
            }
        } catch (Exception e) {
            log.error("Couldn't close ", e);
        }
    }

    public static void closeQuietly(AutoCloseable... autoCloseable) {
        try {
            for (AutoCloseable ac :
                    autoCloseable) {
                if (ac != null) {
                    ac.close();
                }
            }
        } catch (Exception e) {
            log.error("Couldn't close ", e);
        }
    }

    public static void rollBack(EntityManager em) {
        if (em != null) {
            em.getTransaction().rollback();
        }
    }

    public static void rollBack(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
                log.error("Failed to rollback ", e);
            }
        }
    }
}
