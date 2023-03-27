package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static cfg.config.DATE_TIME_FORMATTER;
import static cfg.config.DEFAULT_CHARSET;

public class ServerLogsUtils {
    private ServerLogsUtils() {
    }

    public static void sendServerMassage(String sm) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        sb.append(" | Server: ");

        if (sm.contains("*")) {
            sb.append(sm.substring(1));
            saveLogs(sb.toString());
        } else {
            sb.append(sm);
        }
        sb.insert(0, "| Time: ");
        System.out.println(sb);
    }

    private static void saveLogs(String sm) {
        File file = new File("src" + File.separator + "main" + File.separator + "java" + File.separator
                + "log" + File.separator + "logs.log");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, DEFAULT_CHARSET, true))) {
            writer.write(sm + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
