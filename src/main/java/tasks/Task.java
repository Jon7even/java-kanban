package main.java.tasks;

import java.util.Objects;

public class Task {

    protected int id;
    protected TaskType type;
    protected String name;
    protected String description;
    protected TaskStatus status;

    public Task(TaskType type, String name, String description, TaskStatus status) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
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
        return id + "," + type + "," + name + "," + status + "," + description + ",\n";
    }

}
