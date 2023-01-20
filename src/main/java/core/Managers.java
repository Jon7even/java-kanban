package main.java.core;

import java.io.File;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File("src" + File.separator + "main" + File.separator
                + "resources" + File.separator + "task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
