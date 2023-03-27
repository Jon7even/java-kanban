package cfg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.adapters.LocalDateAdapter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class config {
    public static final int PORT_KV = 8077;
    public static final int PORT_HTTP_TASKS = 8080;
    public static final String HOSTNAME = "localhost";
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public static Gson GsonBuilderCreate() {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter());
        return gsonBuilder.create();
    }

}
