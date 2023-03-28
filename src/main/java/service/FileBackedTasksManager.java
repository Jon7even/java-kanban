package service;

import model.*;
import service.exception.ManagerLoadFromFileException;
import service.exception.ManagerSaveException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static cfg.config.DATE_TIME_FORMATTER;
import static cfg.config.DEFAULT_CHARSET;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;
    private static final String columnNamesCSV = "id,type,name,status,description,epic,duration,startTime";

    protected FileBackedTasksManager() {
        this.file = null;
    }

    protected FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);

        if (file.exists()) {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(file),
                    DEFAULT_CHARSET))) {
                final String firstLine = bufferedReader.readLine();

                if (firstLine == null || !firstLine.equals(columnNamesCSV)) {
                    throw new ManagerLoadFromFileException("File is broken, recovery is impossible");
                }

                while (bufferedReader.ready()) {
                    lines.add(bufferedReader.readLine());
                }
            } catch (IOException e) {
                throw new ManagerLoadFromFileException("Error: ", e);
            }

            sb.append(lines.get(lines.size() - 1));

            List<Task> restoredTasks = new ArrayList<>();

            for (int i = 0; i < lines.size() - 2; i++) {
                restoredTasks.add(fromString(lines.get(i)));
            }

            setTaskManagerListTasks(restoredTasks, taskManager);

            if (sb.toString().isBlank()) {
                return taskManager;
            }

            List<Integer> history = historyFromString(sb.toString());

            for (Integer id : history) {
                for (Task task : restoredTasks) {
                    if (task.getId() == id) {
                        taskManager.historyManager.addHistoryTask(task);
                    }
                }
            }
            taskManager.updateYearlyTimeTableAllTasksAndSubtasks();
            taskManager.prioritizedTasks.addAll(taskManager.tasks.values());
            taskManager.prioritizedTasks.addAll(taskManager.subTasks.values());

        }
        return taskManager;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, DEFAULT_CHARSET))) {

            writer.write(columnNamesCSV + "\n");

            for (Task task : tasks.values()) {
                writer.write(task.toString());
            }
            for (Task epic : epicTasks.values()) {
                writer.write(epic.toString());
            }
            for (Task subtask : subTasks.values()) {
                writer.write(subtask.toString());
            }

            writer.write(" " + "\n");

            if (!historyManager.getHistory().isEmpty()) {
                writer.write(historyToString(historyManager));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Error: ", e);
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

    private static Boolean isLocalDataTime(String dateStr) {
        try {
            DATE_TIME_FORMATTER.parse(dateStr);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static Task fromString(String value) {
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = String.valueOf(values[2]);
        final TaskStatus status = TaskStatus.valueOf(values[3]);
        final String description = String.valueOf(values[4]);
        final long duration = Long.parseLong((values[5]));
        final LocalDateTime startTime;

        if (values.length == 7 || values.length == 8) {
            if (isLocalDataTime(String.valueOf(values[6]))) {
                startTime = LocalDateTime.parse(values[6], DATE_TIME_FORMATTER);
            } else {
                startTime = null;
            }
        } else {
            startTime = null;
        }

        final Task task;

        switch (type) {
            case SUBTASK: {
                final int epicId = Integer.parseInt(String.valueOf(values[7]));
                task = new Subtask(type, name, description, status, duration, startTime, epicId);
                break;
            }
            case EPIC: {
                task = new Epic(type, name, description, status, duration, startTime);
                break;
            }
            case TASK: {
                task = new Task(type, name, description, status, duration, startTime);
                break;
            }
            default: {
                throw new IllegalStateException(String.format("Incorrect task type: %s", type));
            }
        }
        task.setId(id);
        return task;
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

    protected static void setTaskManagerListTasks(List<Task> restoredTasks, FileBackedTasksManager taskManager) {
        int maxId = 0;

        for (Task task : restoredTasks) {
            int currentId = task.getId();
            if (currentId > maxId) {
                maxId = currentId;
            }
            if (task.getType().equals(TaskType.EPIC)) {
                taskManager.epicTasks.put(currentId, (Epic) task);
            } else if (task.getType().equals(TaskType.SUBTASK)) {
                taskManager.subTasks.put(currentId, (Subtask) task);
            } else {
                taskManager.tasks.put(currentId, task);
            }
        }
        for (Integer id : taskManager.subTasks.keySet()) {
            int epicId = taskManager.subTasks.get(id).getRelationEpicId();
            taskManager.epicTasks.get(epicId).addSubtaskId(id);
        }
        taskManager.idGenerate = maxId;
    }

}
