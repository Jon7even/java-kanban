package service.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
