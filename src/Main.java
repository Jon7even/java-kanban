import core.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();
        Task task1 = new Task("Простенькая задача 1", "Отжаться 10000 раз", TaskStatus.NEW);
        final int idTask1 = manager.addNewTask(task1); // добавили простую задачу
        Task task2 = new Task("Простенькая задача 2", "Задача выспаться", TaskStatus.NEW);
        final int idTask2 = manager.addNewTask(task2); // добавили простую задачу
        System.out.println(manager.getTasks()); // распечатываем

       /*manager.deleteTasks();  // удалили все задачи
        System.out.println(manager.getTasks()); // вывели все простые задачи
        System.out.println(manager.getSelectTask(idTask1)); // получили простую 1 задачу
        manager.removeSelectTask(idTask1); // удаляем 1 задачу*/

        Task editing1 = manager.getSelectTask(idTask2); // получили задачу 2
        editing1.setName("Нереальная задача 2"); // редактируем...
        manager.updateTask(editing1); // вносим правки
        System.out.println(manager.getTasks());

        Epic epic1 = new Epic("Эпичная Задача 1", "Подготовка к новому году", TaskStatus.NEW);
        final int epicId1 = manager.addNewEpic(epic1); // добавили простую задачу
        Epic epic2 = new Epic("Эпичная Задача 2", "Дойти до дедлайна", TaskStatus.NEW);
        final int epicId2 = manager.addNewEpic(epic2); // добавили простую задачу
        System.out.println(manager.getEpics());

        /*manager.deleteEpics(); // удалили все эпики
        System.out.println(manager.getEpics()); // вывели все эпики
        System.out.println(manager.getSelectEpic(epicId1)); // получили 1 эпик
        manager.removeSelectEpic(epicId1); // удалили 1 эпик */

        Epic editing2 = manager.getSelectEpic(epicId2);
        editing2.setDescription("Дожили до дедлайна");
        System.out.println(manager.getEpics());

        Subtask subtask1 = new Subtask("1 Подзадача к эпику 1", "Поставить ёлку", TaskStatus.NEW
                , epicId1);
        final int subtaskId1 = manager.addNewSubtask(subtask1); // добавили подзадачу к эпику 1
        Subtask subtask2 = new Subtask("2 Подзадача к эпику 1", "Купить подарки", TaskStatus.NEW
                , epicId1);
        final int subtaskId2 = manager.addNewSubtask(subtask2); // добавили подзадачу к эпику 1
        Subtask subtask3 = new Subtask("1 Подзадача к эпику 2", "Сдать ТЗ на итерацию", TaskStatus.NEW
                , epicId2);
        final int subtaskId3 = manager.addNewSubtask(subtask3); // добавили подзадачу к эпику 2
        System.out.println(manager.getSubtask());
        


       System.out.println(manager);



    }
}
/* Очень запоздалая "Version 1.01" */
