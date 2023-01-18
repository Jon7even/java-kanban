import core.*;
import tasks.*;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        TaskManager manager = Managers.getDefault();


/*       Task task1 = new Task(TaskType.TASK, "Задача 1", "Поменять экран на смартфоне", TaskStatus.NEW);
        final int idTask1 = manager.addNewTask(task1);
        Task task2 = new Task(TaskType.TASK, "Задача 2", "Придумать много Эпиков на весь год",
                TaskStatus.NEW);
        final int idTask2 = manager.addNewTask(task2);

        Epic epic1 = new Epic(TaskType.EPIC, "Эпик 1", "Поменять масло в машине", TaskStatus.NEW);
        final int epicId1 = manager.addNewEpic(epic1);
        Epic epic2 = new Epic(TaskType.EPIC, "Эпик 2", "Придумать Pet проект", TaskStatus.NEW);
        final int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "1 Подзадача к эпику 1", "Купить масло 5w30", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(TaskType.SUBTASK,"2 Подзадача к эпику 1", "Купить хороший фильтр", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask(TaskType.SUBTASK,"3 Подзадача к эпику 1", "Произвести замену", TaskStatus.NEW,
                epicId1);
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
        System.out.println(fileBackedTasksManagerTest.getHistory());



/*        //Шаг 1 - запрашиваем задачи в произвольном порядке
        System.out.println(manager.getTask(idTask1));
        System.out.println(manager.getTask(idTask2));
        System.out.println(manager.getTask(idTask1));
        System.out.println(manager.getEpic(epicId2));
        System.out.println(manager.getTask(idTask1));
        System.out.println(manager.getSubtask(subtaskId3));
        System.out.println(manager.getSubtask(subtaskId1));
        System.out.println(manager.getSubtask(subtaskId2));
        System.out.println(manager.getSubtask(subtaskId3));

        //Шаг 2 - проверяем повторы в истории
        System.out.println(manager.getHistory());

        //Шаг 3 - удаляем задачу и проверяем, что они из истории тоже удалились
        manager.removeTask(idTask1);
        System.out.println(manager.getHistory());

        //Шаг 4 - удаляем эпик с подзадачами и проверяем, что все удалилось из истории
        manager.removeEpic(epicId1);
        System.out.println(manager.getHistory());*/


    }
}
/*Version 2.12*/
