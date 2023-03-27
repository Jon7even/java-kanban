package service;

import static cfg.config.PORT_KV;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager(PORT_KV);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
