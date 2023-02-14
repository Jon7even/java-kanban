package service.exception;

public class ManagerLoadFromFileException extends RuntimeException {
    public ManagerLoadFromFileException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
    public ManagerLoadFromFileException(final String message) {
        super(message);
    }
}
