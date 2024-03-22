package service.exception;

import static service.ServerLogsUtils.sendServerMassage;

public class NetworkingException extends RuntimeException {
    public NetworkingException(final String message, final Throwable throwable) {
        super(message, throwable);
        sendServerMassage(message);
    }

    public NetworkingException(final String message) {
        super(message);
        sendServerMassage(message);
    }
}
