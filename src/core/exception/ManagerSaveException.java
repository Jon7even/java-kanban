package core.exception;

public class ManagerSaveException extends RuntimeException {
    private ManagerSaveException() {
    }

    public ManagerSaveException(final String message) {
        super(message);
    }
}
