import core.*;
import tasks.*;

import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();
        Task task1 = new Task("Простенькая задача 1", "Отжаться 10000 раз", TaskStatus.NEW);
        final int idTask1 = manager.addNewTask(task1); // добавили простую задачу
        Task task2 = new Task("Простенькая задача 2", "Помыть посуду", TaskStatus.IN_PROGRESS);
        final int idTask2 = manager.addNewTask(task2); // добавили простую задачу
        manager.deleteTasks();  // удалили все ненужные задачи

        Task task3 = new Task("Простенькая задача 3", "Отжаться 1 раз", TaskStatus.IN_PROGRESS);
        final int idTask3 = manager.addNewTask(task3); // добавили простую задачу
        Task task4 = new Task("Простенькая задача 4", "Нереальная задача выспаться", TaskStatus.NEW);
        final int idTask4 = manager.addNewTask(task4); // добавили простую задачу

        System.out.println(manager.getTasks()); // вывели все простые задачи

        System.out.println(manager.getSelectTask(idTask3)); // получили простую 3 задачу

        manager.removeSelectTask(idTask3); // удаляем 3 задачу

        System.out.println(manager.getTasks()); // проверяем удалилась ли задача

        Task editing1 = manager.getSelectTask(idTask4);
        editing1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(editing1);

        /*Task editing1 = new Task("Простенькая задача 4", "Очень нереальная задача выспаться",
                TaskStatus.IN_PROGRESS ); // редактируем задачу
        manager.updateTask(editing1); // передаём отредактированную задачу и идентификатор

        System.out.println(manager.getTasks()); // проверяем обновилась ли задача*/


        Task task5 = new Task("Простенькая задача 5", "Погладить одежду", TaskStatus.NEW);
        final int idTask5 = manager.addNewTask(task5); // добавили простую задачу

        System.out.println(manager.getTasks());


        // System.out.println(manager);




/* Готово:
        Создание. Сам объект должен передаваться в качестве параметра.
        Удаление всех задач.
        Получение списка всех задач.
        Получение по идентификатору.
        Удаление по идентификатору.
        Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.*/

        /*  В процессе:


         */


    }
}
/* Очень запоздалая "Version 1.01" */
