package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int relationEpicId;

    public Subtask(TaskType type, String name, String description, TaskStatus status, int relationEpicId) {
        super(type, name, description, status, 0L, null);
        this.relationEpicId = relationEpicId;
    }

    public Subtask(int id, TaskType type, String name, String description, TaskStatus status, long duration,
                   LocalDateTime startTime, int relationEpicId) {
        super(type, name, description, status, duration, startTime);
        this.id = id;
        this.relationEpicId = relationEpicId;
    }

    public Subtask(TaskType type, String name, String description, TaskStatus status, long duration,
                   LocalDateTime startTime, int relationEpicId) {
        super(type, name, description, status, duration, startTime);
        this.relationEpicId = relationEpicId;
    }

    public int getRelationEpicId() {
        return this.relationEpicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return relationEpicId == subtask.relationEpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relationEpicId);
    }

    @Override
    public String toString() {
        String startTimeToString = "";
        if (startTime != null) {
            startTimeToString = startTime.format(DATE_TIME_FORMATTER);
        }
        return id + "," + type + "," + name + "," + status + "," + description + "," + duration + ","
                + startTimeToString + "," + relationEpicId + ",\n";
    }

}
