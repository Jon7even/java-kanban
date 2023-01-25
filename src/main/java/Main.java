package main.java;

import main.java.core.*;
import main.java.tasks.*;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        File file = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator
                + "task.csv");
        FileBackedTasksManager tasksManagerTest = FileBackedTasksManager.loadFromFile(file);

        Task task1 = new Task(TaskType.TASK, "Задача 1", "Поменять экран на смартфоне", TaskStatus.NEW);
        final int idTask1 = tasksManagerTest.addNewTask(task1);
        Task task2 = new Task(TaskType.TASK, "Задача 2", "Придумать много Эпиков на весь год",
                TaskStatus.IN_PROGRESS);
        tasksManagerTest.addNewTask(task2);

        Epic epic1 = new Epic(TaskType.EPIC, "Эпик 1", "Поменять масло в машине", TaskStatus.NEW);
        final int epicId1 = tasksManagerTest.addNewEpic(epic1);
        Epic epic2 = new Epic(TaskType.EPIC, "Эпик 2", "Придумать Pet проект", TaskStatus.NEW);
        final int epicId2 = tasksManagerTest.addNewEpic(epic2);

        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 1", "Купить масло 5w30",
                TaskStatus.DONE, epicId1);
        final Integer subtaskId1 = tasksManagerTest.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "2 Подзадача к эпику 1",
                "Купить хороший фильтр", TaskStatus.DONE, epicId1);
        tasksManagerTest.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask(TaskType.SUBTASK, "3 Подзадача к эпику 1", "Произвести замену",
                TaskStatus.IN_PROGRESS, epicId1);
        final Integer subtaskId3 = tasksManagerTest.addNewSubtask(subtask3);

        System.out.println(tasksManagerTest.getTask(idTask1));
        System.out.println(tasksManagerTest.getEpic(epicId2));
        System.out.println(tasksManagerTest.getSubtask(subtaskId1));
        System.out.println(tasksManagerTest.getSubtask(subtaskId3));
        System.out.println(tasksManagerTest.getHistory());
        System.out.println(tasksManagerTest.getTasks());
        System.out.println(tasksManagerTest.getEpics());
        System.out.println(tasksManagerTest.getSubtasks());
        System.out.println(tasksManagerTest.getHistory());

        FileBackedTasksManager fileBackedTasksManagerTest = FileBackedTasksManager.loadFromFile(file);
        System.out.println(fileBackedTasksManagerTest.getHistory());
        System.out.println(fileBackedTasksManagerTest.getTasks());
        System.out.println(fileBackedTasksManagerTest.getEpics());
        System.out.println(fileBackedTasksManagerTest.getSubtasks());
        System.out.println(fileBackedTasksManagerTest.getSubtask(subtaskId3));

    }

}
