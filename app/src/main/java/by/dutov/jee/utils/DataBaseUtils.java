package by.dutov.jee.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class DataBaseUtils {

    public static void closeQuietly(AutoCloseable... closeable) {
        try {
            for (AutoCloseable ac :
                    closeable) {
                if (ac != null) {
                    ac.close();
                }
            }
        } catch (Exception e) {
            log.error("Couldn't close ", e);
        }
    }

    public static void rollBack(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.rollback();
        } catch (SQLException e) {
            log.error("Failed to rollback ", e);
        }
    }
}
