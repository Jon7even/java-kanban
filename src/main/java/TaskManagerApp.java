import model.*;
import service.HttpTaskManager;
import service.Managers;
import service.servers.HttpTaskServer;
import service.servers.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;

public class TaskManagerApp {
    public static void main(String[] args) throws IOException {
        KVServer kvServer = Managers.getDefaultKVServer();
        kvServer.runServer();
        HttpTaskManager taskManagerHttp = Managers.getDefault();
        HttpTaskServer httpTaskServer = Managers.getDefaultTaskServer(taskManagerHttp);
        httpTaskServer.runServer();

        Task task1 = new Task(TaskType.TASK, "Задача 1", "Описание Задача 1",
                TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 1, 1, 0, 2));
        final int idTask1 = taskManagerHttp.addNewTask(task1);

        Task task2 = new Task(TaskType.TASK, "Задача 2", "Описание Задача 2",
                TaskStatus.IN_PROGRESS, 10,
                LocalDateTime.of(2023, 1, 1, 1, 2));
        final int idTask2 = taskManagerHttp.addNewTask(task2);

        System.out.println(taskManagerHttp.getTask(idTask2));

        Task taskUpdate1 = new Task(TaskType.TASK, "Задача 1", "Описание Задача 1",
                TaskStatus.IN_PROGRESS, 15,
                LocalDateTime.of(2023, 1, 1, 2, 3));
        taskUpdate1.setId(idTask1);
        taskManagerHttp.updateTask(taskUpdate1);

        Epic epic1 = new Epic(3, TaskType.EPIC, "Эпик 1", "описание Эпик 1", TaskStatus.NEW);
        final int epicId1 = taskManagerHttp.addNewEpic(epic1);

        Epic epic2 = new Epic(4, TaskType.EPIC, "Эпик 2", "описание Эпик 2", TaskStatus.NEW);
        final int epicId2 = taskManagerHttp.addNewEpic(epic2);

        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 1", "Описание Подзадачи 1",
                TaskStatus.DONE, 15,
                LocalDateTime.of(2023, 1, 2, 1, 3), epicId1);
        final Integer subtaskId1 = taskManagerHttp.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "2 Подзадача к эпику 1", "Описание Подзадачи 2",
                TaskStatus.IN_PROGRESS, 15,
                LocalDateTime.of(2023, 1, 3, 1, 32), epicId1);
        final Integer subtaskId2 = taskManagerHttp.addNewSubtask(subtask2);

        System.out.println(taskManagerHttp.getSubtask(subtaskId2));

        Subtask subtaskUpdate1 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 1",
                "Описание Подзадачи 1 Изменили", TaskStatus.DONE, 15,
                LocalDateTime.of(2023, 1, 4, 4, 3), epicId1);
        subtaskUpdate1.setId(subtaskId1);
        taskManagerHttp.updateSubtask(subtaskUpdate1);

        HttpTaskManager serializedTasksManager = taskManagerHttp.loadFromHttp();
        System.out.println(taskManagerHttp.getEpic(epicId2));
        System.out.println(serializedTasksManager.getTasks());
        System.out.println(serializedTasksManager.getEpics());
        System.out.println(serializedTasksManager.getSubtasks());
        System.out.println(serializedTasksManager.getHistory());
        System.out.println(serializedTasksManager.getPrioritizedTasks());
    }
}
