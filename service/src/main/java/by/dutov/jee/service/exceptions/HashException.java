package by.dutov.jee.service.exceptions;

public class HashException extends Exception{

    public HashException(String message) {
        super(message);
    }

    public HashException(String message, Throwable cause) {
        super(message, cause);
    }
}
