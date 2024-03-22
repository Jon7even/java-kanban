package service;

import model.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addHistoryTask(Task task);

    void removeHistoryTask(int id);
}
