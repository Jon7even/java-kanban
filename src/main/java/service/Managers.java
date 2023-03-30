package service;

import service.servers.HttpTaskServer;
import service.servers.KVServer;

import java.io.IOException;

import static cfg.config.PORT_KV;

public class Managers {
    private Managers() {
    }

    public static HttpTaskManager getDefault() {
        return new HttpTaskManager(PORT_KV);
    }

    public static KVServer getDefaultKVServer() throws IOException {
        return new KVServer();
    }

    public static HttpTaskServer getDefaultTaskServer(HttpTaskManager httpTaskManager) throws IOException {
        return new HttpTaskServer(httpTaskManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
