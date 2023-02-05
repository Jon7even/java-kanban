package main.java.core;

import main.java.core.exception.ManagerAddTaskException;
import main.java.core.exception.ManagerTimeIntersectionsException;
import main.java.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static main.java.tasks.Task.DATE_TIME_FORMATTER;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epicTasks = new HashMap<>();
    protected final Map<Integer, Subtask> subTasks = new HashMap<>();
    protected final Map<Integer, Boolean> yearlyTimeTable = new HashMap<>();

    public Map<Integer, Boolean> getYearlyTimeTable() { // убрать после проверок:
        return yearlyTimeTable;
    }

    //protected final TreeMap<LocalDateTime, Task> prioritizedTasks = new TreeSet<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int idGenerate = 0;

    public InMemoryTaskManager() {
        for (int i = 0; i < 60 / 15 * 24 * 365; i++) {
            yearlyTimeTable.put((i + 1), false);
        }
    }

    @Override
    public int addNewTask(Task task) {
        try {
            if (!isConflictTimeIntersection(task.getStartTime(), task.getEndTime())) {
                int id = ++idGenerate;
                task.setId(id);
                tasks.put(id, task);
                return id;
            } else {
                throw new ManagerTimeIntersectionsException("Task overlap in time. Conflict in period: "
                        + task.getStartTime().format(DATE_TIME_FORMATTER) + " - "
                        + task.getEndTime().format(DATE_TIME_FORMATTER));
            }
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Task null cannot be passed: ", e);
        }
    }

    @Override
    public int addNewEpic(Epic epic) {
        try {
            int id = ++idGenerate;
            epic.setId(id);
            epicTasks.put(id, epic);
            return id;
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Epic null cannot be passed: ", e);
        }
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        try {
            if (!isConflictTimeIntersection(subtask.getStartTime(), subtask.getEndTime())) {
                Epic epic = getEpic(subtask.getRelationEpicId());
                if (epic == null) {
                    return -1;
                } else {
                    int id = ++idGenerate;
                    subtask.setId(id);
                    subTasks.put(id, subtask);

                    if (epic.getEndTime() == null) {
                        epic.setStartTime(subtask.getStartTime());
                        epic.setEndTime(subtask.getEndTime());
                    } else {
                        if (epic.getStartTime().isAfter(subtask.getStartTime())) {
                            epic.setStartTime(subtask.getStartTime());
                        }
                        if (epic.getEndTime().isBefore(subtask.getEndTime())) {
                            epic.setEndTime(subtask.getEndTime());
                        }
                    }
                    epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()).toMinutes());
                    epic.addSubtaskId(id);
                    updateEpic(epic);
                    return id;
                }
            } else {
                throw new ManagerTimeIntersectionsException("SubTask overlap in time. Conflict in period: "
                        + subtask.getStartTime().format(DATE_TIME_FORMATTER) + " - "
                        + subtask.getEndTime().format(DATE_TIME_FORMATTER));
            }
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Subtask null cannot be passed: ", e);
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
        try {
            tasks.put(task.getId(), task);
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Task null cannot be passed: ", e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        try {
            epicTasks.put(epic.getId(), epic);
            updateEpicStatus(epic);
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Epic null cannot be passed: ", e);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        try {
            subTasks.put(subtask.getId(), subtask);
            Epic epic = getEpic(subtask.getRelationEpicId());
            updateEpicStatus(epic);
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Subtask null cannot be passed: ", e);
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

    private Boolean isConflictTimeIntersection(LocalDateTime timeStart, LocalDateTime timeEnd) {
        if (timeStart == timeEnd) {
            throw new ManagerTimeIntersectionsException("Duration of the task cannot be 0");
        }
        List<Integer> listsInterval = new ArrayList<>();

        int startPeriod = calculateDayOfYear(timeStart.getDayOfYear())
                + calculateMinutesDay(LocalTime.from(timeStart)) / 15;
        int endPeriod = calculateDayOfYear(timeEnd.getDayOfYear()) + calculateMinutesDay(LocalTime.from(timeEnd)) / 15;

        if (startPeriod == endPeriod) {
            listsInterval.add(startPeriod);
        } else {
            for (int i = startPeriod; i <= endPeriod; i++) {
                listsInterval.add(i);
            }
        }

        for (Integer period : listsInterval) {
            if (isBusyIntervalYearlyTimeTable(period)) {
                return true;
            }
        }

        for (Integer period : listsInterval) {
            setIntervalYearlyTimeTable(period, true);
        }
        return false;
    }

    private int calculateDayOfYear(int dayOfYear) {
        if (dayOfYear == 1) {
            return 1;
        } else {
            return 60 / 15 * 24 * dayOfYear;
        }
    }

    private int calculateMinutesDay(LocalTime timeDay) {
        LocalTime defaultTimeDay = LocalTime.of(0, 0);
        return (int) Duration.between(defaultTimeDay, timeDay).toMinutes();
    }

    public void setIntervalYearlyTimeTable(int period, boolean isBusy) {
        yearlyTimeTable.put(period, isBusy);
    }

    private Boolean isBusyIntervalYearlyTimeTable(int period) {
        return yearlyTimeTable.get(period);
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
}
