package service.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Task;
import service.TaskManager;
import service.adapters.LocalDateAdapter;
import service.exception.HttpTaskServerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static service.ServerLogsUtils.sendServerMassage;
import static service.adapters.LocalDateAdapter.DATE_TIME_FORMATTER;

public class HttpTaskServer {
    HttpServer server;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final TaskManager fileTaskManager;

    public HttpTaskServer(TaskManager tm) throws IOException{
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter());
        gson = gsonBuilder.create();
        this.fileTaskManager = tm;
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
    }

    public void runServer() {
        server.start();
        sendServerMassage("HttpTaskServer запущен и прослушивает порт " + PORT);
    }

    private void handler(HttpExchange h){
        try (h){
            final String path = h.getRequestURI().getPath().substring(7);
            switch (path) {
                case "" -> {
                    if (h.getRequestMethod().equals("GET")) {
                        sendServerMassage("Клиент сделал запрос на получение приоритетных задач");
                        final TreeSet<Task> pTasks = fileTaskManager.getPrioritizedTasks();
                        final String response = gson.toJson(pTasks);
                        h.getResponseHeaders().add("X-TM-Method", "getPrioritizedTasks");
                        sendResponse(h, response, 200);
                        sendServerMassage("Успешно обработан запрос на получение приоритетных задач");
                    } else {
                        handleError(h, "requestMethodG");
                    }
                }
                case "task" -> handleTask(h);
                case "history" -> {
                    if (h.getRequestMethod().equals("GET")) {
                        sendServerMassage("Клиент сделал запрос на получение истории просмотра задач");
                        final List<Task> history = fileTaskManager.getHistory();
                        final String response = gson.toJson(history);
                        h.getResponseHeaders().add("X-TM-Method", "getHistory");
                        sendResponse(h, response, 200);
                        sendServerMassage("Успешно обработан запрос на получение истории задач");
                    } else {
                        handleError(h, "requestMethodG");
                    }
                }
                default -> handleError(h, "endpoint");
            }
        } catch (Exception e) {
            throw new HttpTaskServerException("Error: ", e);
        }
    }

    private void handleTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET" -> {
                if (query == null) {
                    sendServerMassage("Клиент сделал запрос на получение всех задач");
                    final List<Task> tasks = fileTaskManager.getTasks();
                    final String response = gson.toJson(tasks);
                    h.getResponseHeaders().add("X-TM-Method", "getTasks");
                    sendResponse(h, response, 200);
                    sendServerMassage("Успешно обработан запрос на получение всех задач");
                    return;
                }
                String idQuery = query.substring(3);
                final int id = Integer.parseInt(idQuery);
                sendServerMassage("Клиент сделал запрос на получение задачи с ID=" + id);
                final Task task = fileTaskManager.getTask(id);
                final String response = gson.toJson(task);
                sendServerMassage("*Получена задача с id=" + id);
                System.out.println("Получена задача с id=" + id);
                h.getResponseHeaders().add("X-TM-Method", "getTask");
                sendResponse(h,response, 200);
                sendServerMassage("Успешно обработан запрос на получение задачи с ID=" + id);
            }
            default -> handleError(h, "requestMethodGPD");
        }
    }

    private void handleError(HttpExchange h, String method) throws IOException {
        String mError = "";
        switch (method) {
            case "requestMethodG" -> {
                mError = "Приложение <b>Task Manager</b> на текущий момент не поддерживает метод - <b>"
                        + h.getRequestMethod() + "</b> на этой странице. <br />"
                        + "Доступные методы: <ul>"
                        + "<li><b>GET</b></li>"
                        + "</ul>";
                sendServerMassage("*Клиент пытался использовать необработанный метод " + h.getRequestMethod()
                        + " на странице: " + h.getRequestURI());
            }
            case "requestMethodGPD" -> {
                mError = "Приложение <b>Task Manager</b> на текущий момент не поддерживает метод - <b>"
                        + h.getRequestMethod() + "</b> на этой странице. <br />"
                        + "Доступные методы: <ul>"
                        + "<li><b>GET</b></li>"
                        + "<li><b>POST</b></li>"
                        + "<li><b>DELETE</b></li>"
                        + "</ul>";
                sendServerMassage("*Клиент пытался использовать необработанный метод " + h.getRequestMethod()
                        + " на странице: " + h.getRequestURI());
            }

            case "endpoint" -> {
                mError = "Запрашиваемый адрес <b>" + "http://localhost:" + PORT + h.getRequestURI()
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
            default -> {
                mError = "Произошла непредсказуемая ошибка. Свяжитесь пожалуйста с администратором сервера";
                sendServerMassage("*Произошла неизвестная ошибка: " + h.getRequestURI());
            }
        }
        sendResponse(h, mError, 404);
    }

    protected void sendResponse(HttpExchange h, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Date", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        h.getResponseHeaders().add("Server", "Java Localhost");
        if (rCode == 200) {
            h.getResponseHeaders().add("Content-Type", "application/json");
        } else {
            h.getResponseHeaders().add("Content-Type", "text/html; charset=" + DEFAULT_CHARSET);
        }
        h.sendResponseHeaders(rCode, resp.length);
        h.getResponseBody().write(resp);
    }

}





















