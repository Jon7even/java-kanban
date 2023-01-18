package core;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {

    public static String toStringTask(Task task) {
        if (task.getType().equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            return subtask.getId() + "," + subtask.getType() + "," + subtask.getName() + "," + subtask.getStatus()
                    + "," + subtask.getDescription() + "," + subtask.getRelationEpicId() + ",\n";
        } else {
            return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus()
                    + "," + task.getDescription() + ",\n";
        }
    }

    public static Task fromString(String value) {
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = String.valueOf(values[2]);
        final TaskStatus status = TaskStatus.valueOf(values[3]);
        final String description = String.valueOf(values[4]);

        if (type.equals(TaskType.SUBTASK)) {
            final int epicId = Integer.parseInt(String.valueOf(values[5]));
            Subtask task = new Subtask(type, name, description, status, epicId);
            task.setId(id);
            return task;
        } else if (type.equals(TaskType.EPIC)) {
            Epic task = new Epic(type, name, description, status);
            task.setId(id);
            return task;
        } else {
            Task task = new Task(type, name, description, status);
            task.setId(id);
            return task;
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder str = new StringBuilder();
        for (Task task : history) {
            str.append(task.getId()).append(",");
        }
        return str.toString();
/*        String str = "";
        for (Task task: history) {
            str = str + task.getId() + ",";
        }
        return str;*/
    }

    public static List<Integer> historyFromString(String value) {
        final String[] values = value.split(",");
        List<Integer> list = new ArrayList<>();
        for (String idTask : values) {
            list.add(Integer.parseInt(idTask));
        }
        return list;
    }
}
