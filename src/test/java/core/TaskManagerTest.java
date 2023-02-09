package test.java.core;

import main.java.core.TaskManager;
import main.java.tasks.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic1;
    protected Epic epic2;
    protected Subtask subtask1;
    protected Subtask subtask2;

    protected void initTasks() {

        task = new Task(1, TaskType.TASK, "Test Task", "Task test description",
                TaskStatus.NEW, 15, LocalDateTime.now());

        epic1 = new Epic(2, TaskType.EPIC, "Test Epic 1", "Epic 1 test description",
                TaskStatus.NEW);

        epic2 = new Epic(3, TaskType.EPIC, "Test Epic 2", "Epic 2 test description",
                TaskStatus.NEW);

        subtask1 = new Subtask(4, TaskType.SUBTASK, "Test Subtask 1", "Subtask 1 test description",
                TaskStatus.NEW, 30, task.getEndTime(), epic1.getId());

        subtask2 = new Subtask(5, TaskType.SUBTASK, "Test Subtask 1", "Subtask 2 test description",
                TaskStatus.NEW, 15, subtask1.getEndTime(), epic1.getId());
    }


}