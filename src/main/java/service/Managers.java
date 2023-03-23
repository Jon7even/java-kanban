package service;


import java.io.File;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        File file = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator
                + "task.csv");
        return new FileBackedTasksManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
