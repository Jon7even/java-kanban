package main.java.core.exception;

public class ManagerLoadFromFileException extends RuntimeException {
    public ManagerLoadFromFileException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
