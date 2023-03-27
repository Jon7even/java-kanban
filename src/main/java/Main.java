import model.*;
import service.FileBackedTasksManager;
import service.HttpTaskManager;
import service.Managers;
import service.servers.HttpTaskServer;
import service.servers.KVServer;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static cfg.config.PORT_KV;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator
                + "task.csv");

/*        Task task1 = new Task(TaskType.TASK, "Задача 1", "Описание Задача 1",
                TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 1, 1, 0, 2));
        final int idTask1 = tasksManagerTest.addNewTask(task1);

        Task task2 = new Task(TaskType.TASK, "Задача 2", "Описание Задача 2",
                TaskStatus.IN_PROGRESS, 15,
                LocalDateTime.of(2023, 1, 1, 0, 31));
        final int idTask2 = tasksManagerTest.addNewTask(task2);

        System.out.println(tasksManagerTest.getTask(idTask2));

        Task taskUpdate1 = new Task(TaskType.TASK, "Задача 1", "Описание Задача 1",
                TaskStatus.IN_PROGRESS, 15,
                LocalDateTime.of(2023, 1, 1, 0, 3));
        taskUpdate1.setId(idTask1);
        tasksManagerTest.updateTask(taskUpdate1);

        Epic epic1 = new Epic(TaskType.EPIC, "Эпик 1", "описание Эпик 1", TaskStatus.NEW);
        final int epicId1 = tasksManagerTest.addNewEpic(epic1);

        Epic epic2 = new Epic(TaskType.EPIC, "Эпик 2", "описание Эпик 2", TaskStatus.NEW);
        final int epicId2 = tasksManagerTest.addNewEpic(epic2);

        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 1", "Описание Подзадачи 1",
                TaskStatus.DONE, 15,
                LocalDateTime.of(2023, 1, 1, 1, 3), epicId1);
        final Integer subtaskId1 = tasksManagerTest.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "2 Подзадача к эпику 1", "Описание Подзадачи 2",
                TaskStatus.IN_PROGRESS, 15,
                LocalDateTime.of(2023, 1, 1, 1, 32), epicId1);
        final Integer subtaskId2 = tasksManagerTest.addNewSubtask(subtask2);

        System.out.println(tasksManagerTest.getSubtask(subtaskId2));

        Subtask subtaskUpdate1 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 1",
                "Описание Подзадачи 1", TaskStatus.DONE, 15,
                LocalDateTime.of(2023, 1, 1, 4, 3), epicId1);
        subtaskUpdate1.setId(subtaskId1);
        tasksManagerTest.updateSubtask(subtaskUpdate1);

        System.out.println(tasksManagerTest.getPrioritizedTasks());*/

        FileBackedTasksManager tasksManagerFBTest = FileBackedTasksManager.loadFromFile(file);
        System.out.println(tasksManagerFBTest.getTasks());
        System.out.println(tasksManagerFBTest.getEpics());
        System.out.println(tasksManagerFBTest.getSubtasks());
        System.out.println(tasksManagerFBTest.getHistory());
        System.out.println(tasksManagerFBTest.getPrioritizedTasks());

        new KVServer().start();
        HttpTaskServer server = new HttpTaskServer();
        server.runServer();

        HttpTaskManager taskServer = new HttpTaskManager(PORT_KV);
        taskServer.put();
        taskServer.load();
    }
}
