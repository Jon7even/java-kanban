package core;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idGenerate = 0;

    public InMemoryTaskManager() {
    }

    @Override
    public int addNewTask(Task task) {
        int id = ++idGenerate;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = ++idGenerate;
        epic.setId(id);
        epicTasks.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Epic epic = getEpic(subtask.getRelationEpicId());
        if (epic == null) {
            System.out.println("Такого Эпика нет, где-то подкралась ошибка.");
            return -1;
        } else {
            int id = ++idGenerate;
            subtask.setId(id);
            subTasks.put(id, subtask);
            epic.addSubtaskId(id);
            updateEpicStatus(epic);
            return id;
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTaskForEpic(int id) {
        Epic epic = getEpic(id);
        if (epic == null) {
            System.out.println("Такого Эпика нет, где-то подкралась ошибка.");
            return null;
        } else {
            ArrayList<Subtask> subtask = new ArrayList<>();
            ArrayList<Integer> search = epic.getRelationSubtaskId();
            for (Integer i : search) {
                subtask.add(getSubtask(i));
            }
            return subtask;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Ошибка");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicTasks.get(epic.getId()) != null) {
            epicTasks.put(epic.getId(), epic);
            updateEpicStatus(epic);
        } else {
            System.out.println("Ошибка");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subTasks.get(subtask.getId()) != null) {
            subTasks.put(subtask.getId(), subtask);
            Epic epic = getEpic(subtask.getRelationEpicId());
            updateEpicStatus(epic);
        } else {
            System.out.println("Ошибка");
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epicTasks.values()) {
            if (!epic.getRelationSubtaskId().isEmpty()) {
                epic.getRelationSubtaskId().clear();
            }
        }
        subTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epicTasks.clear();
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null) {
            return tasks.get(id);
        } else {
            System.out.println("Задача с идентификатором " + id + " не найдена!");
        }
        return null;
    }

    @Override
    public Task getTaskAddLogs(int id) {
        if (tasks.get(id) != null) {
            historyManager.addHistoryTask(tasks.get(id));
            return tasks.get(id);
        } else {
            System.out.println("Задача с идентификатором " + id + " не найдена!");
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epicTasks.get(id) != null) {
            return epicTasks.get(id);
        } else {
            System.out.println("Эпик с идентификатором " + id + " не найден!");
        }
        return null;
    }

    @Override
    public Epic getEpicAddLogs(int id) {
        if (epicTasks.get(id) != null) {
            historyManager.addHistoryTask(epicTasks.get(id));
            return epicTasks.get(id);
        } else {
            System.out.println("Эпик с идентификатором " + id + " не найден!");
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subTasks.get(id) != null) {
            return subTasks.get(id);
        } else {
            System.out.println("Подзадачи с идентификатором " + id + " не найдено!");
        }
        return null;
    }

    @Override
    public Subtask getSubtaskAddLogs(int id) {
        if (subTasks.get(id) != null) {
            historyManager.addHistoryTask(subTasks.get(id));
            return subTasks.get(id);
        } else {
            System.out.println("Подзадачи с идентификатором " + id + " не найдено!");
        }
        return null;
    }

    @Override
    public void removeTask(int id) {
        if (tasks.get(id) != null) {
            tasks.remove(id);
            historyManager.removeHistoryTask(id);
        } else {
            System.out.println("Задача с идентификатором " + id + " не найдена!");
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epicTasks.get(id) != null) {
            for (Integer i : getEpic(id).getRelationSubtaskId()) {
                subTasks.remove(i);
                historyManager.removeHistoryTask(i);
            }
            epicTasks.remove(id);
            historyManager.removeHistoryTask(id);
        } else {
            System.out.println("Эпик с идентификатором " + id + " не найден!");
        }
    }

    @Override
    public void removeSubtask(Integer id) {
        if (subTasks.get(id) != null) {
            Epic epic = getEpic(subTasks.get(id).getRelationEpicId());
            epic.getRelationSubtaskId().remove(id);
            updateEpicStatus(epic);
            subTasks.remove(id);
            historyManager.removeHistoryTask(id);
        } else {
            System.out.println("Эпик с идентификатором " + id + " не найден!");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        boolean isAllSubtaskNew = false;
        boolean isAllSubtaskDone = false;

        ArrayList<Integer> search = epic.getRelationSubtaskId();
        int counterSubtaskNew = 0;
        int counterSubtaskDone = 0;
        for (Integer i : search) {
            Task task = getSubtask(i);
            if (task.getStatus() == TaskStatus.NEW) {
                counterSubtaskNew++;
            }
            if (task.getStatus() == TaskStatus.DONE) {
                counterSubtaskDone++;
            }
        }
        if (counterSubtaskNew == search.size()) {
            isAllSubtaskNew = true;
        }
        if (counterSubtaskDone == search.size()) {
            isAllSubtaskDone = true;
        }

        if (epic.getRelationSubtaskId().isEmpty() || isAllSubtaskNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isAllSubtaskDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager manager = (InMemoryTaskManager) o;
        return idGenerate == manager.idGenerate && tasks.equals(manager.tasks) && epicTasks.equals(manager.epicTasks)
                && subTasks.equals(manager.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, epicTasks, subTasks, idGenerate);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "tasks=" + tasks +
                ", epicTasks=" + epicTasks +
                ", subTasks=" + subTasks +
                ", idGenerate=" + idGenerate +
                '}' + "\n";
    }
}
