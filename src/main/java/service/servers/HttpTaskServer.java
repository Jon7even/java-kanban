package service.servers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.*;
import service.Managers;
import service.TaskManager;
import service.exception.NetworkingException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static cfg.config.*;
import static service.ServerLogsUtils.sendServerMassage;

public class HttpTaskServer {
    HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        gson = GsonBuilderCreate();
        this.taskManager = Managers.getDefault();
        server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT_HTTP_TASKS), 0);
        server.createContext("/tasks", this::handler);
    }

    public void runServer() {
        server.start();
        sendServerMassage("HttpTaskServer запущен и прослушивает порт: " + PORT_HTTP_TASKS);
    }

    private void handler(HttpExchange h) {
        try (h) {
            final String path = h.getRequestURI().getPath().substring(7);
            switch (path) {
                case "" -> {
                    if (h.getRequestMethod().equals(REQUEST_GET)) {
                        sendServerMassage("Клиент сделал запрос " + h.getRequestMethod()
                                + " на получение Приоритетных задач");
                        final TreeSet<Task> pTasks = taskManager.getPrioritizedTasks();
                        final String response = gson.toJson(pTasks);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getPrioritizedTasks");
                        sendResponse(h, response, 200);
                        sendServerMassage("Успешно обработан запрос на получение Приоритетных задач");
                    } else {
                        handleError(h, "requestMethodG", 404);
                    }
                }
                case "task" -> handleTask(h);
                case "epic" -> handleEpic(h);
                case "subtask" -> handleSubtask(h);
                case "subtask/epic" -> {
                    if (h.getRequestMethod().equals(REQUEST_GET)) {
                        String idQuery = h.getRequestURI().getQuery().substring(3);
                        final int id = Integer.parseInt(idQuery);
                        sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod() + " на получение всех "
                                + "Подзадач у Эпика с ID=" + id);
                        final List<Subtask> allSubtasksEpic = taskManager.getAllSubTaskForEpic(id);
                        final String response = gson.toJson(allSubtasksEpic);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getAllSubTaskForEpic");
                        sendResponse(h, response, 200);
                        if (response.equals("[]")) {
                            sendServerMassage("*Запрос на получение Подзадач у Эпика с ID=" + id + " обработан, "
                                    + "но вернулся пустой список");
                        } else if (response.equals("null")) {
                            sendServerMassage("*При получении Подзадач у Эпика с ID=" + id + " клиенту вернулось "
                                    + "значение null");
                        } else {
                            sendServerMassage("Успешно обработан запрос на получение Подзадач у Эпика с ID=" + id);
                        }
                    } else {
                        handleError(h, "requestMethodG", 404);
                    }
                }
                case "history" -> {
                    if (h.getRequestMethod().equals(REQUEST_GET)) {
                        sendServerMassage("Клиент сделал запрос " + h.getRequestMethod()
                                + " на получение Истории просмотра задач");
                        final List<Task> history = taskManager.getHistory();
                        final String response = gson.toJson(history);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getHistory");
                        sendResponse(h, response, 200);
                        sendServerMassage("Успешно обработан запрос на получение Истории просмотра задач");
                    } else {
                        handleError(h, "requestMethodG", 404);
                    }
                }
                default -> handleError(h, "endpoint", 404);
            }
        } catch (IOException e) {
            throw new NetworkingException("*HttpTaskServer: во время выполнения HttpExchange произошла ошибка: ", e);
        }
    }

    private void handleTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case REQUEST_GET -> {
                if (query == null) {
                    sendServerMassage("Клиент сделал запрос " + h.getRequestMethod() + " на получение всех Задач");
                    final List<Task> tasks = taskManager.getTasks();
                    final String response = gson.toJson(tasks);
                    h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getTasks");
                    sendResponse(h, response, 200);
                    sendServerMassage("Успешно обработан запрос на получение всех Задач");
                    return;
                }
                String idQuery = query.substring(3);
                final int id = Integer.parseInt(idQuery);
                sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod()
                        + " на получение Задачи с ID=" + id);
                final Task task = taskManager.getTask(id);
                final String response = gson.toJson(task);
                h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getTask");
                sendResponse(h, response, 200);

                if (!response.equals("null")) {
                    sendServerMassage("Успешно обработан запрос на получение Задачи с ID=" + id);
                } else {
                    sendServerMassage("*Задачи с ID=" + id + " не существует. Клиенту выдано значение NULL");
                }
            }
            case REQUEST_POST -> {
                sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod()
                        + " на создание/обновление Задачи на странице: " + h.getRequestURI());
                Optional<JsonElement> jsonElement = getJsonFromRequest(h);
                if (jsonElement.isPresent()) {
                    final Task oldTask;
                    final Task task = gson.fromJson(jsonElement.get(), Task.class);
                    final int idTask = task.getId();

                    if (!isValidCheckingFields(task, TaskType.TASK)) {
                        sendServerMassage("*При попытке добавить/обновить Задачу с номером ID=" + idTask
                                + " у клиента произошла ошибка: какое-то из полей имеет недопустимое значение!");
                        handleError(h, "validCheckingFields", 400);
                        return;
                    }

                    if (idTask == 0) {
                        int newId = taskManager.addNewTask(task);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "addNewTask");
                        sendServerMassage("*В ТМ на сервере успешно создана новая Задача: "
                                + taskManager.getTask(newId).toString());
                        sendResponse(h, "Добавлена новая Задача", 201);
                    } else {
                        oldTask = taskManager.getTask(idTask);
                        taskManager.updateTask(task);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "updateTask");
                        sendServerMassage("*В ТМ на сервере клиент обновил Задачу ID=" + idTask);
                        sendServerMassage("*Старая версия Задачи: " + oldTask);
                        sendServerMassage("*Новая версия Задачи: " + task);
                        sendResponse(h, "Задача с ID=" + idTask + " обновлена", 200);
                    }
                } else {
                    handleError(h, "badRequest", 400);
                }
            }
            case REQUEST_DELETE -> {
                if (query == null) {
                    sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod() + " на удаление всех Задач");
                    if (taskManager.getTasks().isEmpty()) {
                        final String response = "Список Задач уже пуст. Повторить это действие невозможно";
                        sendResponse(h, response, 400);
                        sendServerMassage("*Клиент пытался удалить все Задачи, но список уже пуст");
                        return;
                    }
                    taskManager.deleteAllTasks();
                    final String response = "Вы удалили все Задачи";
                    h.getResponseHeaders().add(TASK_MANAGER_METHOD, "deleteAllTasks");
                    sendResponse(h, response, 200);
                    sendServerMassage("*С сервера ТМ были удалены все Задачи");
                    return;
                }
                String idQuery = query.substring(3);
                final int id = Integer.parseInt(idQuery);
                sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod()
                        + " на удаление Задачи с ID=" + id);
                if (taskManager.getTask(id) == null) {
                    final String response = "Задачи с таким ID=" + id + " не существует.";
                    sendResponse(h, response, 400);
                    sendServerMassage("*Клиент пытался удалить Задачу с ID=" + id + " но ее не существует");
                    return;
                }
                taskManager.removeTask(id);
                final String response = "Вы удалили Задачу с ID=" + id;
                h.getResponseHeaders().add(TASK_MANAGER_METHOD, "removeTask");
                sendResponse(h, response, 200);
                sendServerMassage("*С сервера ТМ Задача с ID=" + id + " удалена");
            }
            default -> handleError(h, "requestMethodGPD", 404);
        }
    }

    private void handleSubtask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case REQUEST_GET -> {
                if (query == null) {
                    sendServerMassage("Клиент сделал запрос " + h.getRequestMethod()
                            + " на получение всех Подзадач");
                    final List<Subtask> subtasks = taskManager.getSubtasks();
                    final String response = gson.toJson(subtasks);
                    h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getSubtasks");
                    sendResponse(h, response, 200);
                    sendServerMassage("Успешно обработан запрос на получение всех Подзадач");
                    return;
                }
                String idQuery = query.substring(3);
                final int id = Integer.parseInt(idQuery);
                sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod()
                        + " на получение Подзадачи с ID=" + id);
                final Subtask subtask = taskManager.getSubtask(id);
                final String response = gson.toJson(subtask);
                h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getSubtask");
                sendResponse(h, response, 200);
                if (!response.equals("null")) {
                    sendServerMassage("Успешно обработан запрос на получение Подзадачи с ID=" + id);
                } else {
                    sendServerMassage("*Подзадачи с ID=" + id + " не существует. Клиенту выдано значение NULL");
                }
            }
            case REQUEST_POST -> {
                sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod()
                        + " на создание/обновление Подзадачи на странице: " + h.getRequestURI());
                Optional<JsonElement> jsonElement = getJsonFromRequest(h);
                if (jsonElement.isPresent()) {
                    final Subtask oldSubtask;
                    final Subtask subtask = gson.fromJson(jsonElement.get(), Subtask.class);
                    final int idSubtask = subtask.getId();

                    if (!isValidCheckingFields(subtask, TaskType.SUBTASK)) {
                        sendServerMassage("*При попытке добавить/обновить Подзадачу с номером ID=" + idSubtask
                                + " у клиента произошла ошибка: какое-то из полей имеет недопустимое значение!");
                        handleError(h, "validCheckingFields", 400);
                        return;
                    }

                    if (idSubtask == 0) {
                        int newId = taskManager.addNewSubtask(subtask);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "addNewSubtask");
                        if (newId == -1) {
                            sendServerMassage("*При попытке добавить Подзадачу с номером ID=" + idSubtask
                                    + " у клиента произошла ошибка: такого Эпика не существует");
                            handleError(h, "subtaskNotFoundEpic", 400);
                        }
                        sendServerMassage("*В ТМ на сервере успешно создана новая Подзадача: "
                                + taskManager.getTask(newId).toString());
                        sendResponse(h, "Добавлена новая Подзадача", 201);
                    } else {
                        oldSubtask = taskManager.getSubtask(idSubtask);
                        taskManager.updateSubtask(subtask);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "updateSubtask");
                        sendServerMassage("*В ТМ на сервере клиент обновил Подзадачу ID=" + idSubtask);
                        sendServerMassage("*Старая версия Подзадачи: " + oldSubtask);
                        sendServerMassage("*Новая версия Подзадачи: " + subtask);
                        sendResponse(h, "Подзадача с ID=" + idSubtask + " обновлена", 200);
                    }
                } else {
                    handleError(h, "badRequest", 400);
                }
            }
            case REQUEST_DELETE -> {
                if (query == null) {
                    sendServerMassage("*Клиент сделал запрос "
                            + h.getRequestMethod() + " на удаление всех Подзадач");
                    if (taskManager.getSubtasks().isEmpty()) {
                        final String response = "Список Подзадач уже пуст. Повторить это действие невозможно";
                        sendResponse(h, response, 400);
                        sendServerMassage("*Клиент пытался удалить все Подзадачи, но список уже пуст");
                        return;
                    }
                    taskManager.deleteAllSubtasks();
                    final String response = "Вы удалили все Подзадачи";
                    h.getResponseHeaders().add(TASK_MANAGER_METHOD, "deleteAllSubtasks");
                    sendResponse(h, response, 200);
                    sendServerMassage("*С сервера ТМ были удалены все Подзадачи");
                    return;
                }
                String idQuery = query.substring(3);
                final int id = Integer.parseInt(idQuery);
                sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod()
                        + " на удаление Подзадачи с ID=" + id);
                if (taskManager.getSubtask(id) == null) {
                    final String response = "Подзадачи с таким ID=" + id + " не существует.";
                    sendResponse(h, response, 400);
                    sendServerMassage("*Клиент пытался удалить Подзадачу с ID=" + id + " но ее не существует");
                    return;
                }
                taskManager.removeSubtask(id);
                final String response = "Вы удалили Подзадачу с ID=" + id;
                h.getResponseHeaders().add(TASK_MANAGER_METHOD, "removeSubtask");
                sendResponse(h, response, 200);
                sendServerMassage("*С сервера ТМ Подзадача с ID=" + id + " удалена");
            }
            default -> handleError(h, "requestMethodGPD", 404);
        }
    }

    private void handleEpic(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case REQUEST_GET -> {
                if (query == null) {
                    sendServerMassage("Клиент сделал " + h.getRequestMethod() + " запрос на получение всех Эпиков");
                    final List<Epic> epics = taskManager.getEpics();
                    final String response = gson.toJson(epics);
                    h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getEpics");
                    sendResponse(h, response, 200);
                    sendServerMassage("Успешно обработан запрос на получение всех Эпиков");
                    return;
                }
                String idQuery = query.substring(3);
                final int id = Integer.parseInt(idQuery);
                sendServerMassage("*Клиент сделал " + h.getRequestMethod()
                        + " запрос на получение Эпика с ID=" + id);
                final Epic epic = taskManager.getEpic(id);
                final String response = gson.toJson(epic);
                h.getResponseHeaders().add(TASK_MANAGER_METHOD, "getEpic");
                sendResponse(h, response, 200);
                if (!response.equals("null")) {
                    sendServerMassage("Успешно обработан запрос на получение Эпика с ID=" + id);
                } else {
                    sendServerMassage("*Эпика с ID=" + id + " не существует. Клиенту выдано значение NULL");
                }
            }
            case REQUEST_POST -> {
                sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod()
                        + " на создание/обновление Эпика на странице: " + h.getRequestURI());
                Optional<JsonElement> jsonElement = getJsonFromRequest(h);
                if (jsonElement.isPresent()) {
                    final Epic oldEpic;
                    final Epic epic = gson.fromJson(jsonElement.get(), Epic.class);
                    final int idEpic = epic.getId();

                    if (!isValidCheckingFields(epic, TaskType.EPIC)) {
                        sendServerMassage("*При попытке добавить/обновить Эпик с номером ID=" + idEpic
                                + " у клиента произошла ошибка: какое-то из полей имеет недопустимое значение!");
                        handleError(h, "validCheckingFields", 400);
                        return;
                    }

                    if (idEpic == 0) {
                        int newId = taskManager.addNewEpic(epic);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "addNewEpic");
                        sendServerMassage("*В ТМ на сервере успешно создан новый Эпик: "
                                + taskManager.getEpic(newId).toString());
                        sendResponse(h, "Добавлен новый Эпик", 201);
                    } else {
                        oldEpic = taskManager.getEpic(idEpic);
                        taskManager.updateEpic(epic);
                        h.getResponseHeaders().add(TASK_MANAGER_METHOD, "updateEpic");
                        sendServerMassage("*В ТМ на сервере клиент обновил Эпик ID=" + idEpic);
                        sendServerMassage("*Старая версия Эпика: " + oldEpic);
                        sendServerMassage("*Новая версия Эпик: " + epic);
                        sendResponse(h, "Задача с ID=" + idEpic + " обновлена", 200);
                    }
                } else {
                    handleError(h, "badRequest", 400);
                }
            }
            case REQUEST_DELETE -> {
                if (query == null) {
                    sendServerMassage("*Клиент сделал запрос "
                            + h.getRequestMethod() + " на удаление всех Эпиков");
                    if (taskManager.getEpics().isEmpty()) {
                        final String response = "Список Эпиков уже пуст. Повторить это действие невозможно";
                        sendResponse(h, response, 400);
                        sendServerMassage("*Клиент пытался удалить все Эпики, но список уже пуст");
                        return;
                    }
                    taskManager.deleteAllEpics();
                    final String response = "Вы удалили все Эпики";
                    h.getResponseHeaders().add(TASK_MANAGER_METHOD, "deleteAllEpics");
                    sendResponse(h, response, 200);
                    sendServerMassage("*С сервера ТМ были удалены все Эпики");
                    return;
                }
                String idQuery = query.substring(3);
                final int id = Integer.parseInt(idQuery);
                sendServerMassage("*Клиент сделал запрос " + h.getRequestMethod()
                        + " на удаление Эпика с ID=" + id);
                if (taskManager.getEpic(id) == null) {
                    final String response = "Эпика с таким ID=" + id + " не существует.";
                    sendResponse(h, response, 400);
                    sendServerMassage("*Клиент пытался удалить Эпик с ID=" + id + " но его не существует");
                    return;
                }
                taskManager.removeEpic(id);
                final String response = "Вы удалили Эпик с ID=" + id;
                h.getResponseHeaders().add(TASK_MANAGER_METHOD, "removeEpic");
                sendResponse(h, response, 200);
                sendServerMassage("*С сервера ТМ Эпик с ID=" + id + " удален");
            }
            default -> handleError(h, "endpoint", 404);
        }
    }

    private Optional<JsonElement> getJsonFromRequest(HttpExchange h) throws IOException {
        List<String> acceptJson = h.getRequestHeaders().get("Accept");
        String body = new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        JsonElement jsonElement = JsonParser.parseString(body);
        if ((acceptJson != null) && acceptJson.contains(CONTENT_JSON) && jsonElement.isJsonObject()) {
            return Optional.of(jsonElement);
        }
        return Optional.empty();
    }

    protected void sendResponse(HttpExchange h, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Date", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        h.getResponseHeaders().add("Server", "Java Localhost");
        if (rCode == 200) {
            h.getResponseHeaders().add(CONTENT_TYPE, CONTENT_JSON);
        } else {
            h.getResponseHeaders().add(CONTENT_TYPE, "text/html; charset=" + DEFAULT_CHARSET);
        }
        h.sendResponseHeaders(rCode, resp.length);
        h.getResponseBody().write(resp);
    }

    private void handleError(HttpExchange h, String method, int rCode) throws IOException {
        String mError;
        switch (method) {
            case "requestMethodG" -> {
                mError = "Приложение <b>Task Manager</b> на текущий момент не поддерживает метод - <b>"
                        + h.getRequestMethod() + "</b> на этой странице. <br />"
                        + "Доступные методы: <ul>"
                        + "<li><b>" + REQUEST_GET + "</b></li>"
                        + "</ul>";
                sendServerMassage("*Клиент пытался использовать необработанный метод " + h.getRequestMethod()
                        + " на странице: " + h.getRequestURI());
            }
            case "requestMethodGPD" -> {
                mError = "Приложение <b>Task Manager</b> на текущий момент не поддерживает метод - <b>"
                        + h.getRequestMethod() + "</b> на этой странице. <br />"
                        + "Доступные методы: <ul>"
                        + "<li><b>" + REQUEST_GET + "</b></li>"
                        + "<li><b>" + REQUEST_POST + "</b></li>"
                        + "<li><b>" + REQUEST_DELETE + "</b></li>"
                        + "</ul>";
                sendServerMassage("*Клиент пытался использовать необработанный метод " + h.getRequestMethod()
                        + " на странице: " + h.getRequestURI());
            }
            case "endpoint" -> {
                mError = "Запрашиваемый адрес <b>" + "http://localhost:" + PORT_HTTP_TASKS + h.getRequestURI()
                        + "</b> не существует. "
                        + "Доступные страницы: <ul>"
                        + "<a href=\"\\tasks\\\"><li>Priority Tasks</a></li>"
                        + "<a href=\"\\tasks\\task\"><li>ALL Tasks</a></li>"
                        + "<a href=\"\\tasks\\subtask\"><li>ALL Subtasks</a></li>"
                        + "<a href=\"\\tasks\\epic\"><li>ALL Epic</a></li>"
                        + "<a href=\"\\tasks\\history\"><li>History</a></li>"
                        + "</ul>";
                sendServerMassage("*Клиент пытался зайти на несуществующую страницу: " + h.getRequestURI());
            }
            case "badRequest" -> {
                mError = "Вы передаете неправильные данные для добавления/обновления Задач. "
                        + "Возможные причины: <ul>"
                        + "<li><b>В заголовок не передан \"Accept\", \"" + CONTENT_JSON + "\"</b></li>"
                        + "<li><b>Передается пустое Body</b></li>"
                        + "<li><b>Что-то передается, но это не Json объект</b></li>"
                        + "</ul>";
                sendServerMassage("*Клиент в Body или Header попытался передать вместо Json другие данные или "
                        + "null URI: " + h.getRequestURI());
            }
            case "subtaskNotFoundEpic" -> {
                mError = "Произошла ошибка при добавлении/обновлении Подзадачи."
                        + "Возможные причины: <ul>"
                        + "<li><b>Указан ID несуществующего Эпика</b></li>"
                        + "<li><b>Во время обновления/добавления, Эпик существовал, но вы с другого устройства "
                        + "уже удалили Эпик с таким ID.</b></li>"
                        + "</ul> <b>Попробуйте создать Эпик с таким ID, а потом заново добавить/обновить эту Задачу</b";
                sendServerMassage("*Клиент пытался добавить/обновить Подзадачу с несуществующим ID Эпика");
            }
            case "validCheckingFields" -> {
                mError = "Произошла ошибка при добавлении/обновлении Задачи/Подзадачи/Эпика."
                        + " Возможные причины: <ul>"
                        + "<li><b>Использованы недопустимые данные в полях Модели Задач</b></li>"
                        + "<li><b>Какое-то поле указано null </b></li>"
                        + "</ul> <b>Попробуйте найти ошибку в полях Задачи и отправить запрос еще раз.</b";
                sendServerMassage("*Клиент пытался добавить/обновить Задачу/Подзадачу/Эпик "
                        + "с недопустимыми данными в полях Модели Задач");
            }
            default -> {
                mError = "Произошла непредсказуемая ошибка. Свяжитесь пожалуйста с администратором сервера";
                sendServerMassage("*Произошла неизвестная ошибка: " + h.getRequestURI());
            }
        }
        sendResponse(h, mError, rCode);
    }

    private Boolean isValidCheckingFields(Task task, TaskType taskTypeValid) {
        boolean isTaskTypeIsValid = task.getType() == taskTypeValid;
        boolean isTaskHaveType = Arrays.stream(TaskType.values()).anyMatch(taskType -> taskType == task.getType());
        boolean isTaskHaveStatus = Arrays.stream(TaskStatus.values())
                .anyMatch(taskStatus -> taskStatus == task.getStatus());
        boolean isHaveName = !task.getName().isEmpty();
        boolean isHaveDescription = !task.getDescription().isEmpty();
        return isTaskTypeIsValid && isTaskHaveType && isTaskHaveStatus && isHaveName && isHaveDescription;
    }
}