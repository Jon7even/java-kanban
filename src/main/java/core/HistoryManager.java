package main.java.core;

import java.util.List;

import main.java.tasks.Task;

public interface HistoryManager {
    List<Task> getHistory();

    void addHistoryTask(Task task);

    void removeHistoryTask(int id);

}
