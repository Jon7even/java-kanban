package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static service.adapters.LocalDateAdapter.DATE_TIME_FORMATTER;

public class Epic extends Task {
    protected final ArrayList<Integer> relationSubtaskId = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(TaskType type, String name, String description, TaskStatus status) {
        super(type, name, description, status, 0L, null);
        this.endTime = startTime;
    }

    public Epic(int id, TaskType type, String name, String description, TaskStatus status) {
        super(type, name, description, status, 0L, null);
        this.id = id;
        this.endTime = startTime;
    }

    public Epic(TaskType type, String name, String description, TaskStatus status, long duration,
                LocalDateTime startTime) {
        super(type, name, description, status, duration, startTime);
        this.endTime = startTime;
    }

    public void addSubtaskId(int id) {
        relationSubtaskId.add(id);
    }

    public ArrayList<Integer> getRelationSubtaskId() {
        return relationSubtaskId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(relationSubtaskId, epic.relationSubtaskId) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relationSubtaskId, endTime);
    }

    @Override
    public String toString() {
        String startTimeToString = "";
        if (startTime != null) {
            startTimeToString = startTime.format(DATE_TIME_FORMATTER);
        }
        return id + "," + type + "," + name + "," + status + "," + description + "," + duration + ","
                + startTimeToString + ",\n";
    }

}
