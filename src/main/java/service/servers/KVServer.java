package service.servers;

import static cfg.config.*;
import static service.ServerLogsUtils.sendServerMassage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class KVServer {
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT_KV), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        sendServerMassage("KVTaskClient сделал запрос на выгрузку данных с KVServer");
        if (!hasAuth(h)) {
            sendServerMassage("*Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
            h.sendResponseHeaders(403, 0);
            return;
        }
        if (REQUEST_GET.equals(h.getRequestMethod())) {
            String key = h.getRequestURI().getPath().substring("/load/".length());
            if (key.isEmpty()) {
                sendServerMassage("*Key для получения данных пустой. key указывается в пути: /load/{key}");
                h.sendResponseHeaders(400, 0);
                return;
            }
            if (!data.containsKey(key)) {
                sendServerMassage("*KVTaskClient пытается получить данные по несуществующему ключу: " + key);
                h.sendResponseHeaders(404, 0);
                return;
            }
            sendText(h, data.get(key));
            sendServerMassage("KVServer отправил данные по ключу: " + key);
            h.sendResponseHeaders(200, 0);
        } else {
            errorInMethod(h, REQUEST_GET);
        }
    }

    private void save(HttpExchange h) throws IOException {
        try (h) {
            sendServerMassage("KVTaskClient сделал запрос на сохранение данных ТМ на KVServer");
            if (!hasAuth(h)) {
                sendServerMassage("*Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if (REQUEST_POST.equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    sendServerMassage("*Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    sendServerMassage("*Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                sendServerMassage("KVServer успешно создал/обновил в БД значение для ключа " + key);
                h.sendResponseHeaders(200, 0);
            } else {
                errorInMethod(h, REQUEST_POST);
            }
        }
    }

    private void register(HttpExchange h) throws IOException {
        try (h) {
            sendServerMassage("KVTaskClient сделал запрос на выдачу токена /register");
            if (REQUEST_GET.equals(h.getRequestMethod())) {
                sendText(h, apiToken);
                sendServerMassage("KVServer выдал токен: " + apiToken);
            } else {
                errorInMethod(h, REQUEST_GET);
            }
        }
    }

    public void runServer() {
        sendServerMassage("API_TOKEN: " + apiToken);
        server.start();
        sendServerMassage("KVServer запущен и прослушивает порт: " + PORT_KV);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    private void errorInMethod(HttpExchange h, String isExpected) throws IOException {
        sendServerMassage("KVServer ожидает запрос - " + isExpected
                + " а KVTaskClient отправил неправильный запрос - " + h.getRequestMethod());
        h.sendResponseHeaders(405, 0);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add(CONTENT_TYPE, CONTENT_JSON);
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
