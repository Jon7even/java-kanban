package main.java.core.exception;

public class HistoryManagerAddTask extends RuntimeException {
    public HistoryManagerAddTask(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
