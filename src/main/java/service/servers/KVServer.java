package service.servers;

import static java.nio.charset.StandardCharsets.UTF_8;
import static service.ServerLogsUtils.sendServerMassage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
    public static final int PORT = 8077;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        sendServerMassage("*Клиент сделал запрос на выгрузку данных с KVServer");
        if (!hasAuth(h)) {
            sendServerMassage("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
            h.sendResponseHeaders(403, 0);
            return;
        }
        if ("GET".equals(h.getRequestMethod())) {
            String key = h.getRequestURI().getPath().substring("/load/".length());
            if (key.isEmpty()) {
                sendServerMassage("Key для получения данных пустой. key указывается в пути: /load/{key}");
                h.sendResponseHeaders(400, 0);
                return;
            }
			if (!data.containsKey(key)) {
				sendServerMassage("*Клиент пытается получить данные по несуществующему ключу: " + key);
				h.sendResponseHeaders(404, 0);
				return;
			}
			sendText(h, data.get(key));
            sendServerMassage("*KVServer отправил данные клиенту по ключу: " + key);
            h.sendResponseHeaders(200, 0);
        } else {
            errorInMethod(h, "GET");
        }
    }

    private void save(HttpExchange h) throws IOException {
        try (h) {
            sendServerMassage("*Клиент сделал запрос на сохранение данных ТМ на сервер KVServer");
            if (!hasAuth(h)) {
                sendServerMassage("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    sendServerMassage("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    sendServerMassage("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                sendServerMassage("*KVServer успешно создал/обновил в БД значение для ключа " + key);
                h.sendResponseHeaders(201, 0);
            } else {
                errorInMethod(h, "POST");
            }
        }
    }

    private void register(HttpExchange h) throws IOException {
        try (h) {
            sendServerMassage("Клиент сделал запрос на выдачу токена /register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
                sendServerMassage("*Клиент получил токен: " + apiToken);
            } else {
                errorInMethod(h, "GET");
            }
        }
    }

    public void start() {
        sendServerMassage("API_TOKEN: " + apiToken);
        server.start();
        sendServerMassage("KVServer запущен и прослушивает порт: " + PORT);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void errorInMethod(HttpExchange h, String isExpected) throws IOException {
        sendServerMassage("Сервер ожидает запрос - " + isExpected
                + " а клиент отправил неправильный запрос - " + h.getRequestMethod());
        h.sendResponseHeaders(405, 0);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
