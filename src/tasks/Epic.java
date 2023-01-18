package tasks;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected final ArrayList<Integer> relationSubtaskId = new ArrayList<>();

    public Epic(TaskType type, String name, String description, TaskStatus status) {
        super(type, name, description, status);
    }

    public void addSubtaskId(int id) {
        relationSubtaskId.add(id);
    }

    public ArrayList<Integer> getRelationSubtaskId() {
        return relationSubtaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return relationSubtaskId.equals(epic.relationSubtaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relationSubtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "relationSubtaskId=" + Arrays.toString(new ArrayList[]{relationSubtaskId}) +
                ", id=" + id +
                ", TypeTask='" + type + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}' + "\n";
    }

}
