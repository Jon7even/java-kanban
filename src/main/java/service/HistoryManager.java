package service;

import java.util.List;

import model.Task;

public interface HistoryManager {
    List<Task> getHistory();

    void addHistoryTask(Task task);

    void removeHistoryTask(int id);
}
