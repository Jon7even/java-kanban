import core.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Простенькая задача 1", "Отжаться 10000 раз", TaskStatus.NEW);
        final int idTask1 = manager.addNewTask(task1); // добавили простую Задачу
        Task task2 = new Task("Простенькая задача 2", "Задача выспаться", TaskStatus.NEW);
        final int idTask2 = manager.addNewTask(task2); // добавили простую Задачу
        System.out.println(manager.getTasks()); // распечатываем

       /*manager.deleteAllTasks();  // удалили все Задачи
        System.out.println(manager.getTasks()); // вывели все простые Задачи
        System.out.println(manager.getTask(idTask1)); // получили простую 1 Задачу
        manager.removeTask(idTask1); // удаляем 1 Задачу*/

        Task editing1 = manager.getTask(idTask2); // получили Задачу 2
        editing1.setName("Очень нереальная задача 2"); // редактируем...
        manager.updateTask(editing1); // вносим правки
        System.out.println(manager.getTasks());

        Epic epic1 = new Epic("Эпичная Задача 1", "Подготовка к новому году", TaskStatus.NEW);
        final int epicId1 = manager.addNewEpic(epic1); // добавили простую Задачу
        Epic epic2 = new Epic("Эпичная Задача 2", "Доделать кухню", TaskStatus.NEW);
        final int epicId2 = manager.addNewEpic(epic2); // добавили простую Задачу
        System.out.println(manager.getEpics());

        /*manager.deleteAllEpics(); // удалили все Эпики
        System.out.println(manager.getEpics()); // вывели все Эпики
        System.out.println(manager.getEpic(epicId1)); // получили 1 Эпик
        manager.removeEpic(epicId1); // удалили 1 Эпик */

        Epic editing2 = manager.getEpic(epicId2);
        editing2.setDescription("Доделать кухню в каникулы");
        editing2.setStatus(TaskStatus.IN_PROGRESS); // проверяем на ручное изменение статуса
        manager.updateEpic(editing2);
        System.out.println(manager.getEpics());

        Subtask subtask1 = new Subtask("1 Подзадача к эпику 1", "Поставить ёлку", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1); // добавили подзадачу к эпику 1
        Subtask subtask2 = new Subtask("2 Подзадача к эпику 1", "Купить подарки", TaskStatus.NEW,
                epicId1);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2); // добавили подзадачу к эпику 1
        Subtask subtask3 = new Subtask("1 Подзадача к эпику 2", "Заменить цоколь", TaskStatus.NEW,
                epicId2);
        final Integer subtaskId3 = manager.addNewSubtask(subtask3); // добавили Подзадачу к эпику 2
        System.out.println(manager.getSubtask());

        /* manager.deleteAllSubtasks();// удалили все Подзадачи
        System.out.println(manager.getSubtask()); // вывели все Подзадачи
        System.out.println(manager.getSubtask(subtaskId2)); // получили 1 конкретную подзадачу
        manager.removeSubtask(subtaskId3); // удалили 1 подзадачу*/

        Subtask editing3 = manager.getSubtask(subtaskId1);
        editing3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(editing3);

        Subtask editing4 = manager.getSubtask(subtaskId2);
        editing4.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(editing4);

        Subtask editing5 = manager.getSubtask(subtaskId3);
        editing5.setStatus(TaskStatus.IN_PROGRESS); //Проверяем выполнение подзадачи и перерасчета Эпика
        manager.updateSubtask(editing5);
        System.out.println(manager.getAllSubTaskForEpic(epicId1)); // Получаем Подзадачи определённого Эпика
        System.out.println(manager.getEpics());

        System.out.println("История просмотренных тасков:\n" + manager.getHistory());

    }
}
/*Version 1.04*/
