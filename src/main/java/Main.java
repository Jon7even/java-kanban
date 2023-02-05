package main.java;

import main.java.core.*;
import main.java.tasks.*;

import java.io.File;
import java.time.LocalDateTime;

import static main.java.tasks.Task.DATE_TIME_FORMATTER;

public class Main {

    public static void main(String[] args) {

        File file = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator
                + "task.csv");

        FileBackedTasksManager tasksManagerTest = FileBackedTasksManager.loadFromFile(file);

        Task task1 = new Task(TaskType.TASK, "Задача 1", "Описание Задача 1",
                TaskStatus.NEW, 15, LocalDateTime.of(2023,1, 1,0,2));
        final int idTask1 = tasksManagerTest.addNewTask(task1);
        Task task2 = new Task(TaskType.TASK, "Задача 2", "Описание Задача 2",
                TaskStatus.IN_PROGRESS, 15, LocalDateTime.of(2023,1, 1,0,30));
        tasksManagerTest.addNewTask(task2);
        System.out.println(tasksManagerTest.getTask(idTask1));
        //System.out.println(task2.getEndTime());


        Epic epic1 = new Epic(TaskType.EPIC, "Эпик 1", "описание Эпик 1", TaskStatus.NEW);
        final int epicId1 = tasksManagerTest.addNewEpic(epic1);

        Epic epic2 = new Epic(TaskType.EPIC, "Эпик 2", "описание Эпик 2", TaskStatus.NEW);
        final int epicId2 = tasksManagerTest.addNewEpic(epic2);

        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 1", "Описание Подзадачи 1",
                TaskStatus.DONE, 15, LocalDateTime.of(2023,1, 1,1,1), epicId1);
        final Integer subtaskId1 = tasksManagerTest.addNewSubtask(subtask1);

        System.out.println(tasksManagerTest.getEpic(epicId1).getEndTime().format(DATE_TIME_FORMATTER));

        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "2 Подзадача к эпику 1", "Описание Подзадачи 2",
                TaskStatus.IN_PROGRESS, 15, LocalDateTime.of(2023,1, 1,1,32), epicId1);
        final Integer subtaskId2 = tasksManagerTest.addNewSubtask(subtask2);

        System.out.println(tasksManagerTest.getEpic(epicId1).getEndTime().format(DATE_TIME_FORMATTER));
/*
        Subtask subtask3 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 2", "Описание Подзадачи 1",
                TaskStatus.IN_PROGRESS, 15, LocalDateTime.now(), epicId2);
        final Integer subtaskId3 = tasksManagerTest.addNewSubtask(subtask3);
*/
        System.out.println(tasksManagerTest.getEpic(epicId2));
        System.out.println(tasksManagerTest.getEpic(epicId1));
        System.out.println(tasksManagerTest.getEpics());
/*
        System.out.println(tasksManagerTest.getTask(idTask1));
        System.out.println(tasksManagerTest.getEpic(epicId2));
        System.out.println(tasksManagerTest.getSubtask(subtaskId1));
        System.out.println(tasksManagerTest.getSubtask(subtaskId3));
        System.out.println(tasksManagerTest.getHistory());
        System.out.println(tasksManagerTest.getTasks());
        System.out.println(tasksManagerTest.getEpics());
        System.out.println(tasksManagerTest.getSubtasks());
        System.out.println(tasksManagerTest.getHistory());
*/

        System.out.println(tasksManagerTest.getYearlyTimeTable());
        //FileBackedTasksManager fileBackedTasksManagerTest = FileBackedTasksManager.loadFromFile(file);
        //System.out.println(fileBackedTasksManagerTest.getHistory());
        //System.out.println(fileBackedTasksManagerTest.getTasks());
        // System.out.println(fileBackedTasksManagerTest.getEpics());
        // System.out.println(fileBackedTasksManagerTest.getSubtasks());
        //System.out.println(fileBackedTasksManagerTest.getSubtask(subtaskId3));
        //System.out.println(fileBackedTasksManagerTest.getYearlyTimeTable());
    }

}
