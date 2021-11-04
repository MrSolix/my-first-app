package by.dutov.jee.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloseClass {

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            log.error("Couldn't close {}", closeable);
        }
    }
}
