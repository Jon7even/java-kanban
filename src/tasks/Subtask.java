package tasks;

public class Subtask extends Task {
    protected int subtaskId;
    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }
}
