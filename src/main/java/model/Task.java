package model;

import java.time.LocalDateTime;
import java.util.Objects;

import static cfg.config.DATE_TIME_FORMATTER;

public class Task {
    protected int id;
    protected TaskType type;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected long duration;
    protected LocalDateTime startTime;

    public Task(TaskType type, String name, String description, TaskStatus status) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = 0L;
        this.startTime = null;
    }

    public Task(int id, TaskType type, String name, String description, TaskStatus status, long duration,
                LocalDateTime startTime) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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
        if (startTime != null) {
            return startTime.plusMinutes(duration);
        } else {
            return null;
        }
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
        return id == task.id && duration == task.duration && type == task.type && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, description, status, duration, startTime);
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
