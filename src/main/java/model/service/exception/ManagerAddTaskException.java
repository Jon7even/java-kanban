package service.exception;

public class ManagerAddTaskException extends RuntimeException {
    public ManagerAddTaskException(final String message) {
        super(message);
    }

    public ManagerAddTaskException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}