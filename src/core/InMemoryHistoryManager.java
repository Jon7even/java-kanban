package core;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int LOG_SIZE = 10;
    private final List<Task> tasksPrintLogs = new ArrayList<>();

    InMemoryHistoryManager() {
    }

    @Override
    public List<Task> getHistory() {
        if (!(tasksPrintLogs.isEmpty())) {
            return tasksPrintLogs;
        } else {
            return null;
        }
    }

    @Override
    public void addTask(Task task) {
        tasksPrintLogs.add(task);
        if (tasksPrintLogs.size() > LOG_SIZE) {
            tasksPrintLogs.remove(0);
        }
    }

}