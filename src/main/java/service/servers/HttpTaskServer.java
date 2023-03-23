package service.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Task;
import service.Managers;
import service.TaskManager;
import service.adapters.LocalDateAdapter;
import service.exception.HttpTaskServerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        System.out.println("Сервер запущен на порту " + PORT);
    }

    private void handler(HttpExchange h){
        try (h){
            final String path = h.getRequestURI().getPath().substring(7);
            switch (path) {
                case "task" -> handleTask(h);
                default -> {
                    System.out.println("Такой страницы не существует: " + h.getRequestURI());
                    h.sendResponseHeaders(404,0);
                }
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
                    final List<Task> tasks = fileTaskManager.getTasks();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получены все простые задачи");
                    sendText(h, response);
                    return;
                }
                String idQuery = query.substring(3);
                final int id = Integer.parseInt(idQuery);
                final Task task = fileTaskManager.getTask(id);
                final String response = gson.toJson(task);
                System.out.println("Получена задача с id=" + id);
                sendText(h,response);
            }
            default -> {
                System.out.println("Что-то Вы темните, сударь: " + h.getRequestURI());
                h.sendResponseHeaders(404,0);
            }
        }
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }


}





















