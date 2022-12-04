package core;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    static final int LOG_SIZE = 10; // вдруг нам вздумается изменить вывод потом
    private final List<Task> tasksPrintLogs = new ArrayList<>();

    protected InMemoryHistoryManager() {
    }

    @Override
    public List<Task> getHistory() {
        if (!(tasksPrintLogs.isEmpty())) { // проверка... а нужна ли она? Без неё, ошибки не выводит...
            if (tasksPrintLogs.size() < LOG_SIZE) {
                return tasksPrintLogs; //возвращаем как есть
            } else {
                List<Task> tasks = new ArrayList<>();
                for (int i = tasksPrintLogs.size() - LOG_SIZE; i < tasksPrintLogs.size(); i++) {
                    tasks.add(tasksPrintLogs.get(i));
                }
                return tasks; // возвращаем составленный список
            }
        } else {
            System.out.println("История пуста, Ваша светлость");
            return null;
        }

    }

    @Override
    public void addTask(Task task) {
        tasksPrintLogs.add(task);
    }

}
