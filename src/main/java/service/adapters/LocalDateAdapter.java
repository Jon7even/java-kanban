package service.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

import static cfg.config.DATE_TIME_FORMATTER;

public class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (!(localDateTime == null)) {
            jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
        } else {
            jsonWriter.value((String) null);
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) {
        try {
            return LocalDateTime.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
        } catch (IOException | IllegalStateException exception) {
            return null;
        }
    }
}
