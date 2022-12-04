package core;

public class Managers {

    private Managers (){} // приватный конструктор, чтобы нельзя было создать экземпляр класса
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
