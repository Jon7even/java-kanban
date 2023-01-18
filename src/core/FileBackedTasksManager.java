package core;

import core.exception.*;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static core.CSVTaskFormat.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) throws ManagerLoadFromFileException {
        final FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(file),
                StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerLoadFromFileException("Что-то пошло не так:(");
        }

        sb.append(lines.get(lines.size() - 1));

        List<Task> restoredTasks = new ArrayList<>();

        for (int i = 1; i < lines.size() - 2; i++){
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

    protected void save() throws ManagerSaveException {
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
            throw new ManagerSaveException("Что-то пошло не так:(");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FileBackedTasksManager that = (FileBackedTasksManager) o;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file);
    }

    @Override
    public String toString() {
        return "FileBackedTasksManager{" +
                "file=" + file + "\n" +
                ", idGenerate=" + idGenerate + "\n" +
                ", tasks=" + tasks + "\n" +
                ", epicTasks=" + epicTasks + "\n" +
                ", subTasks=" + subTasks + "\n" +
                ", historyManager=" + historyManager +
                '}' + "\n";
    }
}
