package service;

//import service.http.HttpTaskManager;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
/*    public static TaskManager getDefault() {
        return new HttpTaskManager();
    }*/

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
