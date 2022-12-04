package core;

import java.util.List;
import tasks.Task;

public interface HistoryManager {
    List<Task> getHistory();

    void addTask(Task task);

}
