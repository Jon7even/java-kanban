package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static service.adapters.LocalDateAdapter.DATE_TIME_FORMATTER;

public class ServerLogsUtils {
    private ServerLogsUtils(){
    }

    public static void sendServerMassage(String sm) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        sb.append(" | Server: ");

        if (sm.contains("*")) {
            sb.append(sm.substring(1));
            saveLogs(sb.toString());
        }
        sb.insert(0, "| Time: ");
        System.out.println(sb);
    }

    private static void saveLogs(String sm){
        File file = new File("src" + File.separator + "main" + File.separator + "java" + File.separator
                + "log" + File.separator + "logs.log");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            writer.write(sm + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
