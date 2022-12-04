import core.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Простенькая задача 1", "Отжаться 10000 раз", TaskStatus.NEW);
        final int idTask1 = manager.addNewTask(task1); // добавили простую Задачу
        Task task2 = new Task("Простенькая задача 2", "Задача выспаться", TaskStatus.NEW);
        final int idTask2 = manager.addNewTask(task2); // добавили простую Задачу

        System.out.println(manager.getTaskAddLogs(idTask1)); // просмотрели 1

        Task editing1 = manager.getTask(idTask2); // получили Задачу 2
        editing1.setName("Очень нереальная задача 2"); // редактируем...
        manager.updateTask(editing1); // вносим правки
        System.out.println(manager.getTasks());

        Epic epic1 = new Epic("Эпичная Задача 1", "Подготовка к новому году", TaskStatus.NEW);
        final int epicId1 = manager.addNewEpic(epic1); // добавили простую Задачу
        Epic epic2 = new Epic("Эпичная Задача 2", "Доделать кухню", TaskStatus.NEW);
        final int epicId2 = manager.addNewEpic(epic2); // добавили простую Задачу

        System.out.println(manager.getEpicAddLogs(epicId1)); // просмотрели 2

        Epic editing2 = manager.getEpic(epicId2);
        editing2.setDescription("Доделать кухню в каникулы");
        editing2.setStatus(TaskStatus.IN_PROGRESS);
        Subtask subtask1 = new Subtask("1 Подзадача к эпику 1", "Поставить ёлку", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1); // добавили подзадачу к эпику 1
        Subtask subtask2 = new Subtask("2 Подзадача к эпику 1", "Купить подарки", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2); // добавили подзадачу к эпику 1
        Subtask subtask3 = new Subtask("1 Подзадача к эпику 2", "Заменить цоколь", TaskStatus.NEW,
                epicId2);
        final Integer subtaskId3 = manager.addNewSubtask(subtask3);

        System.out.println(manager.getSubtaskAddLogs(subtaskId3)); //просмотрели 3

        System.out.println(manager.getHistory());

    }
}
/*Version 2.06*/
