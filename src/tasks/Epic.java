package tasks;

public class Epic extends Task {

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}' + "\n";
    }


}
