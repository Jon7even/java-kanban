package tasks;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> relationSubtaskId = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public void addSubtaskId(int id) {
        relationSubtaskId.add(id);
    }

    public ArrayList<Integer> getRelationSubtaskId() {
        return relationSubtaskId;
    }

    @Override
    public void setStatus(TaskStatus status) {
        if (TaskStatus.IN_PROGRESS == status || TaskStatus.DONE == status) {
            System.out.println("Ручное изменение статусов для Эпиков отключено по ТЗ");
        } else {
            System.out.println("Статус уже " + TaskStatus.NEW);
        }
    }

    public void autoSetStatus(TaskStatus status) {
        this.status = status;
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}' + "\n";
    }
}
