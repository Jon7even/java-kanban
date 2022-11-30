package tasks;
import java.util.Objects;

public class Subtask extends Task {
    private final int relationEpicId; // к ID какого эпика относится подзадача

    public Subtask(String name, String description, TaskStatus status, int relationEpicId) {
        super(name, description, status);
        this.relationEpicId = relationEpicId;
    }

    public int getRelationEpicId() {
        return relationEpicId;
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
        return "Subtask{" +
                "relationEpicId=" + relationEpicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}' + "\n";
    }
}
