package service.exception;

public class HttpTaskServerException extends RuntimeException {
    public HttpTaskServerException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
