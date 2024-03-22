package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import service.client.KVTaskClient;
import service.exception.NetworkingException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cfg.config.GsonBuilderCreate;
import static cfg.config.PORT_KV;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(int port) {
        super();
        gson = GsonBuilderCreate();
        this.client = new KVTaskClient(port);
    }

    @Override
    protected void save() {
        try {
            String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
            client.put("tasks", jsonTasks);
            String jsonEpic = gson.toJson(new ArrayList<>(epicTasks.values()));
            client.put("epics", jsonEpic);
            String jsonSubtask = gson.toJson(new ArrayList<>(subTasks.values()));
            client.put("subtasks", jsonSubtask);
            String jsonHistory = gson.toJson(historyManager.getHistory().stream().map(Task::getId)
                    .collect(Collectors.toList()));
            client.put("history", jsonHistory);
        } catch (Exception e) {
            throw new NetworkingException("*HttpTaskManager: во время сохранения данных в БД произошла ошибка: ", e);
        }
    }

    public HttpTaskManager loadFromHttp() {
        try {
            final HttpTaskManager taskManager = new HttpTaskManager(PORT_KV);

            List<Task> restoredTasks = new ArrayList<>();
            ArrayList<Task> tasks = gson.fromJson(client.load("tasks"),
                    new TypeToken<ArrayList<Task>>() {}.getType());
            restoredTasks.addAll(tasks);
            ArrayList<Epic> epics = gson.fromJson(client.load("epics"),
                    new TypeToken<ArrayList<Epic>>() {}.getType());
            restoredTasks.addAll(epics);
            ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"),
                    new TypeToken<ArrayList<Subtask>>() {}.getType());
            restoredTasks.addAll(subtasks);
            setTaskManagerListTasks(restoredTasks, taskManager, false);
            List<Integer> historyIdTasks = gson.fromJson(client.load("history"),
                    new TypeToken<ArrayList<Integer>>() {}.getType());
            setTaskManagerHistoryTasks(restoredTasks, historyIdTasks, taskManager);
            taskManager.updateYearlyTimeTableAllTasksAndSubtasks();
            taskManager.prioritizedTasks.addAll(taskManager.tasks.values());
            taskManager.prioritizedTasks.addAll(taskManager.subTasks.values());
            return taskManager;
        } catch (Exception e) {
            throw new NetworkingException("*HttpTaskManager: во время загрузки данных из БД произошла ошибка: ", e);
        }
    }
}