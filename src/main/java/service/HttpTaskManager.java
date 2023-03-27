package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import service.client.KVTaskClient;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static cfg.config.GsonBuilderCreate;
import static cfg.config.PORT_KV;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(int port) {
        super();
        gson = GsonBuilderCreate();
        client = new KVTaskClient(port);
    }

    public void put() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);
        String jsonEpic = gson.toJson(new ArrayList<>(epicTasks.values()));
        client.put("epics", jsonEpic);
        String jsonSubtask = gson.toJson(new ArrayList<>(subTasks.values()));
        client.put("subtasks", jsonSubtask);
        String jsonHistory = gson.toJson(historyManager.getHistory().stream().map(Task::getId)
                .collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }

    public void load() {
        final HttpTaskManager taskManager = new HttpTaskManager(PORT_KV);
        ArrayList<Task> restoredTasks = new ArrayList<>();
        ArrayList<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {
        }.getType());
        ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {
        }
                .getType());
        ArrayList<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {
        }
                .getType());
        restoredTasks.addAll(tasks);
        restoredTasks.addAll(subtasks);
        restoredTasks.addAll(epics);
        addTasks(restoredTasks, taskManager);
    }

    private TaskManager addTasks(ArrayList<Task> tasks, HttpTaskManager httpTaskManager) {
        readListTasks(tasks, httpTaskManager);
        return httpTaskManager;
    }

}

