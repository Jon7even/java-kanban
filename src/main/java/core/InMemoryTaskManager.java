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
            LocalDateTime startTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();

            if (!isConflictTimeIntersection(startTime, endTime)) {
                setIntervalsYearlyTimeTable(listsInterval(startTime, endTime));
                int id = ++idGenerate;
                task.setId(id);
                tasks.put(id, task);
                return id;
            } else {
                throw new ManagerTimeIntersectionsException("Task overlap in time. Conflict in period: "
                        + startTime.format(DATE_TIME_FORMATTER) + " - "
                        + endTime.format(DATE_TIME_FORMATTER));
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
            LocalDateTime startTime = subtask.getStartTime();
            LocalDateTime endTime = subtask.getEndTime();

            if (!isConflictTimeIntersection(startTime, endTime)) {
                Epic epic = epicTasks.get(subtask.getRelationEpicId());
                if (epic == null) {
                    throw new ManagerAddTaskException("Error, Epic null cannot be passed!");
                } else {
                    setIntervalsYearlyTimeTable(listsInterval(startTime, endTime));
                    int id = ++idGenerate;
                    subtask.setId(id);
                    subTasks.put(id, subtask);
                    epic.addSubtaskId(id);
                    updateEpicTime(epic);
                    updateEpicStatus(epic);
                    return id;
                }
            } else {
                throw new ManagerTimeIntersectionsException("SubTask overlap in time. Conflict in period: "
                        + startTime.format(DATE_TIME_FORMATTER) + " - "
                        + endTime.format(DATE_TIME_FORMATTER));
            }
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Subtask null cannot be passed: ", e);
        }
    }

    private void updateEpicTime(Epic epic) {
        ArrayList<Integer> subtasks = epic.getRelationSubtaskId();

        if (subtasks == null) {
            epic.setDuration(0L);
            epic.setStartTime(null);
        } else {

            for (Integer id : subtasks) {
                Subtask subtask = subTasks.get(id);

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
            }

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
        Epic epic = epicTasks.get(id);
        if (epic == null) {
            throw new ManagerAddTaskException("Error, Epic null cannot be passed!");
        } else {
            ArrayList<Subtask> subtask = new ArrayList<>();
            ArrayList<Integer> search = epic.getRelationSubtaskId();
            for (Integer i : search) {
                subtask.add(subTasks.get(i));
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
            Epic epic = epicTasks.get(subtask.getRelationEpicId());
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
            for (Integer i : epicTasks.get(id).getRelationSubtaskId()) {
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
            Epic epic = epicTasks.get(subTasks.get(id).getRelationEpicId());
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

        for (Integer period : listsInterval(timeStart, timeEnd)) {
            if (isBusyIntervalYearlyTimeTable(period)) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> listsInterval(LocalDateTime timeStart, LocalDateTime timeEnd) {
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
        return listsInterval;
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

    private void setIntervalsYearlyTimeTable(List<Integer> listsInterval) {
        for (Integer period : listsInterval) {
            setPeriodYearlyTimeTable(period, true);
        }
    }

    public void setPeriodYearlyTimeTable(int period, boolean isBusy) {
        yearlyTimeTable.put(period, isBusy);
    }

    private Boolean isBusyIntervalYearlyTimeTable(int period) {
        return yearlyTimeTable.get(period);
    }


    private void updateEpicStatus(Epic epic) {
        boolean isAllSubtaskNew = false;
        boolean isAllSubtaskDone = false;

        ArrayList<Integer> subtasks = epic.getRelationSubtaskId();
        int counterSubtaskNew = 0;
        int counterSubtaskDone = 0;
        for (Integer i : subtasks) {
            Task task = subTasks.get(i);
            if (task.getStatus() == TaskStatus.NEW) {
                counterSubtaskNew++;
            }
            if (task.getStatus() == TaskStatus.DONE) {
                counterSubtaskDone++;
            }
        }
        if (counterSubtaskNew == subtasks.size()) {
            isAllSubtaskNew = true;
        }
        if (counterSubtaskDone == subtasks.size()) {
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
