package by.dutov.jee.utils;

import by.dutov.jee.repository.RepositoryDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataBaseUtils {
    private final RepositoryDataSource repositoryDataSource;

    public void closeAndRemove(Connection con) {
        try {
            if (con != null) {
                con.close();
                repositoryDataSource.removeConnection();
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
