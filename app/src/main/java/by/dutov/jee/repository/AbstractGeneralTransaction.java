package by.dutov.jee.repository;

import by.dutov.jee.repository.person.postgres.ConnectionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGeneralTransaction<T> implements GeneralTransaction<T> {
    protected final ThreadLocal<T> threadLocal = new ThreadLocal<>();
    public static ConnectionType connectionType = ConnectionType.SINGLE;

    public void closeQuietly(AutoCloseable... autoCloseable) {
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
}
