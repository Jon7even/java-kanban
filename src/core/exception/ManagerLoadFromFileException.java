package core.exception;

public class ManagerLoadFromFileException extends RuntimeException {
    private ManagerLoadFromFileException() {
    }

    public ManagerLoadFromFileException(final String message) {
        super(message);
    }
}
