package main.java.core;

import main.java.core.exception.*;
import main.java.tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    protected FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(file),
                StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerLoadFromFileException(e.getMessage());
        }

        sb.append(lines.get(lines.size() - 1));

        List<Task> restoredTasks = new ArrayList<>();

        for (int i = 1; i < lines.size() - 2; i++) {
            restoredTasks.add(fromString(lines.get(i)));
        }

        int maxId = 0;

        for (Task task : restoredTasks) {
            int currentId = task.getId();
            if (currentId > maxId) {
                maxId = currentId;
            }
            if (task.getType().equals(TaskType.EPIC)) {
                tasksManager.epicTasks.put(currentId, (Epic) task);
            } else if (task.getType().equals(TaskType.SUBTASK)) {
                tasksManager.subTasks.put(currentId, (Subtask) task);
            } else {
                tasksManager.tasks.put(currentId, task);
            }
        }

        for (Integer id : tasksManager.subTasks.keySet()) {
            int epicId = tasksManager.getSubtask(id).getRelationEpicId();
            tasksManager.getEpic(epicId).addSubtaskId(id);
        }

        tasksManager.idGenerate = maxId;

        List<Integer> history = historyFromString(sb.toString());

        for (Integer id : history) {
            for (Task task : restoredTasks) {
                if (task.getId() == id) {
                    tasksManager.historyManager.addHistoryTask(task);
                }
            }
        }
        return tasksManager;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            writer.write("id,type,name,status,description,epic" + "\n");

            for (Task task : tasks.values()) {
                writer.write(toStringTask(task));
            }
            for (Task task : epicTasks.values()) {
                writer.write(toStringTask(task));
            }
            for (Task subtask : subTasks.values()) {
                writer.write(toStringTask(subtask));
            }
            writer.write(" " + "\n");
            writer.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTask(int id) {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        save();
    }

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
