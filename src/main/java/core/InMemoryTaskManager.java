package main.java.core;

import main.java.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int idGenerate = 0;

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
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTaskForEpic(int id) {
        Epic epic = getEpic(id);
        if (epic == null) {
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
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicTasks.get(epic.getId()) != null) {
            epicTasks.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subTasks.get(subtask.getId()) != null) {
            subTasks.put(subtask.getId(), subtask);
            Epic epic = getEpic(subtask.getRelationEpicId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer i : tasks.keySet()) {
            historyManager.removeHistoryTask(i);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epicTasks.values()) {
            if (!epic.getRelationSubtaskId().isEmpty()) {
                epic.getRelationSubtaskId().clear();
                updateEpicStatus(epic);
            }
        }
        for (Integer i : subTasks.keySet()) {
            historyManager.removeHistoryTask(i);
        }
        subTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer i : epicTasks.keySet()) {
            historyManager.removeHistoryTask(i);
        }
        deleteAllSubtasks();
        epicTasks.clear();
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null) {
            historyManager.addHistoryTask(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epicTasks.get(id) != null) {
            historyManager.addHistoryTask(epicTasks.get(id));
            return epicTasks.get(id);
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subTasks.get(id) != null) {
            historyManager.addHistoryTask(subTasks.get(id));
            return subTasks.get(id);
        }
        return null;
    }

    @Override
    public void removeTask(int id) {
        if (tasks.get(id) != null) {
            tasks.remove(id);
            historyManager.removeHistoryTask(id);
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
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return idGenerate == that.idGenerate && tasks.equals(that.tasks) && epicTasks.equals(that.epicTasks)
                && subTasks.equals(that.subTasks) && historyManager.equals(that.historyManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, epicTasks, subTasks, historyManager, idGenerate);
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                ", idGenerate=" + idGenerate + "\n" +
                "main.java.tasks=" + tasks + "\n" +
                ", epicTasks=" + epicTasks + "\n" +
                ", subTasks=" + subTasks + "\n" +
                ", historyManager=" + historyManager + "\n" +
                '}' + "\n";
    }
}
