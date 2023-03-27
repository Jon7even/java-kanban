package service.client;

import service.exception.NetworkingException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static cfg.config.HOSTNAME;
import static service.ServerLogsUtils.sendServerMassage;

public class KVTaskClient {
    private final String apiToken;
    private final String url;

    public KVTaskClient(int port) {
        url = "http://" + HOSTNAME + ":" + port + "/";
        apiToken = getToken(url);
    }

    private String getToken(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url + "register")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status != 200) {
                throw new NetworkingException("*KVTaskClient при получении Токена вместо 200 получил " + status);
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            sendServerMassage("*У KVTaskClient во время получения Токена /register произошла ошибка.");
            throw new NetworkingException("Error", e);
        }
    }

    public String load(String key) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(url + "load/"+ key + "?API_TOKEN=" + apiToken)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status != 200) {
                throw new NetworkingException("*KVTaskClient при получении данных /load вместо 200 получил " + status);
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            sendServerMassage("*У KVTaskClient во время получения данных /load произошла ошибка.");
            throw new NetworkingException("Error", e);
        }
    }

    public void put(String key, String value) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "save/"+ key + "?API_TOKEN=" + apiToken))
                .POST(HttpRequest.BodyPublishers.ofString(value)).build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            int status = response.statusCode();
            if (status != 200) {
                throw new NetworkingException("*KVTaskClient при попытке сохранить /save вместо 200 получил " + status);
            }
        } catch (IOException | InterruptedException e) {
            sendServerMassage("*У KVTaskClient во время сохранения данных /save произошла ошибка.");
            throw new NetworkingException("Error", e);
        }
    }

}
















