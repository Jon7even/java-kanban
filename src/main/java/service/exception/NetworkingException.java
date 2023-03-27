package service.exception;

public class NetworkingException extends RuntimeException {
    public NetworkingException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public NetworkingException(final String message) {
        super(message);
    }
}
