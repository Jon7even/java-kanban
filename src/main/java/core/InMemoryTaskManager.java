package main.java.core;

import main.java.core.exception.ManagerAddTaskException;
import main.java.core.exception.ManagerGetTaskException;
import main.java.core.exception.ManagerRemoveTaskException;
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
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);
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
                prioritizedTasks.add(task);
                setIntervalsYearlyTimeTable(listsInterval(startTime, endTime), true);
                int id = ++idGenerate;
                task.setId(id);
                tasks.put(id, task);
                return id;
            } else {
                throw new ManagerTimeIntersectionsException("Task overlap in time. " + "Task id - "
                        + task.getId() + "\n    Conflict in period: "
                        + startTime.format(DATE_TIME_FORMATTER) + " - "
                        + endTime.format(DATE_TIME_FORMATTER));
            }
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Task cannot be passed: ", e);
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
            throw new ManagerAddTaskException("Error, Epic cannot be passed: ", e);
        }
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        try {
            LocalDateTime startTime = subtask.getStartTime();
            LocalDateTime endTime = subtask.getEndTime();

            if (!isConflictTimeIntersection(startTime, endTime)) {
                prioritizedTasks.add(subtask);
                Epic epic = epicTasks.get(subtask.getRelationEpicId());
                if (epic == null) {
                    throw new ManagerGetTaskException("Error, Epic cannot be received!");
                } else {
                    setIntervalsYearlyTimeTable(listsInterval(startTime, endTime), true);
                    int id = ++idGenerate;
                    subtask.setId(id);
                    subTasks.put(id, subtask);
                    epic.addSubtaskId(id);
                    updateEpicTime(epic);
                    updateEpicStatus(epic);
                    return id;
                }
            } else {
                throw new ManagerTimeIntersectionsException("SubTask overlap in time. " + "Subtask id - "
                        + subtask.getId() + "\n    Conflict in period: "
                        + startTime.format(DATE_TIME_FORMATTER) + " - "
                        + endTime.format(DATE_TIME_FORMATTER));
            }
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Subtask cannot be passed: ", e);
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
            throw new ManagerGetTaskException("Error, Epic with id " + id + " cannot be received!");
        } else {
            List<Integer> idSubtasks = epic.getRelationSubtaskId();
            if (idSubtasks.size() != 0) {
                ArrayList<Subtask> subtask = new ArrayList<>();
                for (Integer idGet : idSubtasks) {
                    subtask.add(subTasks.get(idGet));
                }
                return subtask;
            } else {
                throw new ManagerGetTaskException("Error, Subtasks not found!");
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        try {
            int idTask = task.getId();
            LocalDateTime startTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();
            LocalDateTime oldStartTime = tasks.get(idTask).getStartTime();
            LocalDateTime oldEndTime = tasks.get(idTask).getEndTime();
            List<Integer> oldTimeInterval = listsInterval(oldStartTime, oldEndTime);

            prioritizedTasks.remove(tasks.get(idTask));
            prioritizedTasks.add(task);

            if (oldTimeInterval.equals(listsInterval(startTime, endTime))) {
                tasks.put(idTask, task);
            } else {
                setIntervalsYearlyTimeTable(oldTimeInterval, false);
                if (!isConflictTimeIntersection(startTime, endTime)) {
                    setIntervalsYearlyTimeTable(listsInterval(startTime, endTime), true);
                    tasks.put(idTask, task);
                } else {
                    throw new ManagerTimeIntersectionsException("Task overlap in time. " + "Task id - "
                            + task.getId() + "\n    Conflict in period: "
                            + startTime.format(DATE_TIME_FORMATTER) + " - "
                            + endTime.format(DATE_TIME_FORMATTER));
                }
            }
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Task cannot be passed: ", e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        try {
            epicTasks.put(epic.getId(), epic);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Epic cannot be passed: ", e);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        try {
            int idSubtask = subtask.getId();
            LocalDateTime startTime = subtask.getStartTime();
            LocalDateTime endTime = subtask.getEndTime();
            LocalDateTime oldStartTime = subTasks.get(idSubtask).getStartTime();
            LocalDateTime oldEndTime = subTasks.get(idSubtask).getEndTime();
            List<Integer> oldTimeInterval = listsInterval(oldStartTime, oldEndTime);

            prioritizedTasks.remove(subTasks.get(idSubtask));
            prioritizedTasks.add(subtask);

            if (oldTimeInterval.equals(listsInterval(startTime, endTime))) {
                subTasks.put(idSubtask, subtask);
                Epic epic = epicTasks.get(subtask.getRelationEpicId());
                updateEpicStatus(epic);
                updateEpicTime(epic);
            } else {
                setIntervalsYearlyTimeTable(oldTimeInterval, false);
                if (!isConflictTimeIntersection(startTime, endTime)) {
                    setIntervalsYearlyTimeTable(listsInterval(startTime, endTime), true);
                    subTasks.put(idSubtask, subtask);
                    Epic epic = epicTasks.get(subtask.getRelationEpicId());
                    updateEpicStatus(epic);
                    updateEpicTime(epic);
                } else {
                    throw new ManagerTimeIntersectionsException("SubTask overlap in time. " + "Subtask id - "
                            + subtask.getId() + "\n    Conflict in period: "
                            + startTime.format(DATE_TIME_FORMATTER) + " - "
                            + endTime.format(DATE_TIME_FORMATTER));
                }
            }
        } catch (NullPointerException e) {
            throw new ManagerAddTaskException("Error, Subtask cannot be passed: ", e);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {

            historyManager.removeHistoryTask(id);
            prioritizedTasks.remove(tasks.get(id));
            setIntervalsYearlyTimeTable(listsInterval(tasks.get(id).getStartTime(),
                    tasks.get(id).getEndTime()), false);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epicTasks.values()) {
            if (!epic.getRelationSubtaskId().isEmpty()) {
                for (Integer id : epic.getRelationSubtaskId()) {
                    prioritizedTasks.remove(subTasks.get(id));
                    setIntervalsYearlyTimeTable(listsInterval(subTasks.get(id).getStartTime(),
                            subTasks.get(id).getEndTime()), false);
                    historyManager.removeHistoryTask(id);
                }
                epic.getRelationSubtaskId().clear();
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
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
        } else {
            throw new ManagerGetTaskException("Error, Task with id " + id + " cannot be received!");
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epicTasks.get(id) != null) {
            historyManager.addHistoryTask(epicTasks.get(id));
            return epicTasks.get(id);
        } else {
            throw new ManagerGetTaskException("Error, Epic with id " + id + " cannot be received!");
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subTasks.get(id) != null) {
            historyManager.addHistoryTask(subTasks.get(id));
            return subTasks.get(id);
        } else {
            throw new ManagerGetTaskException("Error, Subtask with id " + id + " cannot be received!");
        }
    }

    @Override
    public void removeTask(int id) {
        if (tasks.get(id) != null) {
            setIntervalsYearlyTimeTable(listsInterval(tasks.get(id).getStartTime(), tasks.get(id).getEndTime()),
                    false);
            tasks.remove(id);
            historyManager.removeHistoryTask(id);
        } else {
            throw new ManagerRemoveTaskException("Error, Task with id " + id + " cannot be deleted!");
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
        } else {
            throw new ManagerRemoveTaskException("Error, Epic with id " + id + " cannot be deleted!");
        }
    }

    @Override
    public void removeSubtask(Integer id) {
        if (subTasks.get(id) != null) {
            Epic epic = epicTasks.get(subTasks.get(id).getRelationEpicId());
            epic.getRelationSubtaskId().remove(id);
            updateEpicStatus(epic);
            setIntervalsYearlyTimeTable(listsInterval(subTasks.get(id).getStartTime(), subTasks.get(id).getEndTime()),
                    false);
            subTasks.remove(id);
            historyManager.removeHistoryTask(id);
            updateEpicTime(epic);
        } else {
            throw new ManagerRemoveTaskException("Error, Subtask with id " + id + " cannot be deleted!");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
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

    private void updateEpicTime(Epic epic) {
        ArrayList<Integer> subtasks = epic.getRelationSubtaskId();
        epic.setDuration(0L);
        epic.setStartTime(null);
        epic.setEndTime(null);

        if (subtasks.size() != 0) {

            for (Integer id : subtasks) {
                Subtask subtask = subTasks.get(id);
                LocalDateTime subtaskStartTime = subtask.getStartTime();
                LocalDateTime subtaskEndTime = subtask.getEndTime();

                if (epic.getEndTime() == null) {
                    epic.setStartTime(subtaskStartTime);
                    epic.setEndTime(subtaskEndTime);
                    epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()).toMinutes());
                } else {
                    if (epic.getStartTime().isAfter(subtaskStartTime)) {
                        epic.setStartTime(subtaskStartTime);
                    }
                    if (epic.getEndTime().isBefore(subtaskEndTime)) {
                        epic.setEndTime(subtaskEndTime);
                    }
                    epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()).toMinutes());
                }

            }

        }

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

    protected void updateYearlyTimeTable() {
        for (Task task : tasks.values()) {
            LocalDateTime startTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();

            if (!isConflictTimeIntersection(startTime, endTime)) {
                setIntervalsYearlyTimeTable(listsInterval(startTime, endTime), true);
            } else {
                throw new ManagerTimeIntersectionsException("Task overlap in time. " + "Task id - "
                        + task.getId() + "\n    Conflict in period: "
                        + startTime.format(DATE_TIME_FORMATTER) + " - "
                        + endTime.format(DATE_TIME_FORMATTER));
            }
        }

        for (Subtask subtask : subTasks.values()) {
            LocalDateTime startTime = subtask.getStartTime();
            LocalDateTime endTime = subtask.getEndTime();

            if (!isConflictTimeIntersection(startTime, endTime)) {
                setIntervalsYearlyTimeTable(listsInterval(startTime, endTime), true);
            } else {
                throw new ManagerTimeIntersectionsException("Subtask overlap in time. " + "Subtask id - "
                        + subtask.getId() + "\n    Conflict in period: "
                        + startTime.format(DATE_TIME_FORMATTER) + " - "
                        + endTime.format(DATE_TIME_FORMATTER));
            }
        }

        for (Epic epic : epicTasks.values()) {
            updateEpicTime(epic);
        }

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

    private void setIntervalsYearlyTimeTable(List<Integer> listsInterval, Boolean isBusy) {
        for (Integer period : listsInterval) {
            setPeriodYearlyTimeTable(period, isBusy);
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
