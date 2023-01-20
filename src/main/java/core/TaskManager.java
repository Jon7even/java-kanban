package main.java.core;

import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;

import java.util.List;

public interface TaskManager {
    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getAllSubTaskForEpic(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(Integer id);

    List<Task> getHistory();
}
