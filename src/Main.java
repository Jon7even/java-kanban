import core.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Подарить хорошее настроение!", TaskStatus.NEW);
        final int idTask1 = manager.addNewTask(task1);
        Task task2 = new Task("Задача 2", "Поздравить Ревьюера:) с наступающим 2023!",
                TaskStatus.NEW);
        final int idTask2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Подготовка к новому году", TaskStatus.NEW);
        final int epicId1 = manager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Придумать Pet проект", TaskStatus.NEW);
        final int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("1 Подзадача к эпику 1", "Нарядить ёлку", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("2 Подзадача к эпику 1", "Упаковать подарки", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("3 Подзадача к эпику 1", "Вручить поздравления", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId3 = manager.addNewSubtask(subtask3);

        //Шаг 1 - запрашиваем задачи в произвольном порядке
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
        System.out.println(manager.getHistory());
    }
}
/*Version 2.11*/
