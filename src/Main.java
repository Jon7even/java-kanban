import core.*;
import tasks.*;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

/*        Task task1 = new Task(TaskType.TASK, "Задача 1", "Поменять экран на смартфоне", TaskStatus.NEW);
        final int idTask1 = manager.addNewTask(task1);
        Task task2 = new Task(TaskType.TASK, "Задача 2", "Придумать много Эпиков на весь год",
                TaskStatus.IN_PROGRESS);
        final int idTask2 = manager.addNewTask(task2);

        Epic epic1 = new Epic(TaskType.EPIC, "Эпик 1", "Поменять масло в машине", TaskStatus.NEW);
        final int epicId1 = manager.addNewEpic(epic1);
        Epic epic2 = new Epic(TaskType.EPIC, "Эпик 2", "Придумать Pet проект", TaskStatus.NEW);
        final int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 1", "Купить масло 5w30",
                TaskStatus.DONE, epicId1);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "2 Подзадача к эпику 1",
                "Купить хороший фильтр", TaskStatus.DONE, epicId1);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask(TaskType.SUBTASK, "3 Подзадача к эпику 1", "Произвести замену",
                TaskStatus.IN_PROGRESS, epicId1);
        final Integer subtaskId3 = manager.addNewSubtask(subtask3);

        System.out.println(manager.getTask(idTask1));
        System.out.println(manager.getTask(idTask2));
        System.out.println(manager.getEpic(epicId1));
        System.out.println(manager.getEpic(epicId2));
        System.out.println(manager.getSubtask(subtaskId1));
        System.out.println(manager.getSubtask(subtaskId2));
        System.out.println(manager.getSubtask(subtaskId3));
        System.out.println(manager.getHistory());*/

        File file = new File("src" + File.separator + "resources" + File.separator + "task.csv");
        FileBackedTasksManager fileBackedTasksManagerTest = FileBackedTasksManager.loadFromFile(file);

        System.out.println("Состояние восстановленного менеджера:");
        System.out.println(fileBackedTasksManagerTest.getTask(2));
        System.out.println(fileBackedTasksManagerTest.getHistory());

        System.out.println("Добавляем новую задачу:");
        Task task3 = new Task(TaskType.TASK, "Задача 3", "Проверяем работу программы", TaskStatus.NEW);
        final int idTask3 = fileBackedTasksManagerTest.addNewTask(task3);
        System.out.println(fileBackedTasksManagerTest.getTask(8));
        System.out.println(fileBackedTasksManagerTest.getHistory());
    }
}
/*Version 2.14*/