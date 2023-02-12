package service.exception;

public class HistoryManagerRemoveTask extends RuntimeException {
    public HistoryManagerRemoveTask(final String message) {
        super(message);
    }
}
