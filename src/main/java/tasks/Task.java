package main.java.tasks;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected int id;
    protected TaskType type;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected long duration;
    protected LocalDateTime startTime;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public Task(TaskType type, String name, String description, TaskStatus status) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = 0L;
        this.startTime = null;
    }

    public Task(TaskType type, String name, String description, TaskStatus status, long duration,
                LocalDateTime startTime) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Boolean test() {
        return startTime != null;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public TaskType getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && type == task.type && name.equals(task.name) && description.equals(task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, description, status);
    }

    @Override
    public String toString() {
        String startTimeToString = "";
        if (startTime == null) {
            startTimeToString = "null";
        } else {
            startTimeToString = startTime.format(DATE_TIME_FORMATTER);
        }
        return id + "," + type + "," + name + "," + status + "," + description + "," + duration + ","
                + startTimeToString + ",\n";
    }

}
