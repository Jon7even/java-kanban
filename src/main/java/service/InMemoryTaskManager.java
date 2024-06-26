package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.exception.ManagerTimeIntersectionsException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static cfg.config.DATE_TIME_FORMATTER;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epicTasks = new HashMap<>();
    protected final Map<Integer, Subtask> subTasks = new HashMap<>();
    protected final Map<Integer, Boolean> yearlyTimeTable = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime,
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
        if (task != null) {
            addTaskInPrioritizedTasks(task);
            int id = ++idGenerate;
            task.setId(id);
            tasks.put(id, task);
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public int addNewEpic(Epic epic) {
        if (epic != null) {
            int id = ++idGenerate;
            epic.setId(id);
            epicTasks.put(id, epic);
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            Epic epic = epicTasks.get(subtask.getRelationEpicId());
            if (epic == null) {
                return -1;
            } else {
                addTaskInPrioritizedTasks(subtask);
                int id = ++idGenerate;
                subtask.setId(id);
                subTasks.put(id, subtask);
                epic.addSubtaskId(id);
                updateEpicTime(epic);
                updateEpicStatus(epic);
                return id;
            }
        } else {
            return -1;
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public List<Subtask> getAllSubTaskForEpic(int id) {
        Epic epic = epicTasks.get(id);
        if (epic == null || epic.getRelationSubtaskId().size() == 0) {
            return Collections.emptyList();
        } else {
            List<Integer> idSubtasks = epic.getRelationSubtaskId();
            List<Subtask> subtasks = new ArrayList<>();
            for (Integer idGet : idSubtasks) {
                Subtask subtask = subTasks.get(idGet);
                subtasks.add(subtask);
            }
            return subtasks;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) != null) {
            int idTask = task.getId();
            updateTaskInPrioritizedTasks(task, tasks.get(idTask).getStartTime(), tasks.get(idTask).getEndTime(),
                    tasks.get(task.getId()));
            tasks.put(idTask, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicTasks.get(epic.getId()) != null) {
            epicTasks.put(epic.getId(), epic);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subTasks.get(subtask.getId()) != null) {
            int idSubtask = subtask.getId();
            updateTaskInPrioritizedTasks(subtask, subTasks.get(idSubtask).getStartTime(),
                    subTasks.get(idSubtask).getEndTime(), subTasks.get(idSubtask));
            subTasks.put(idSubtask, subtask);
            Epic epic = epicTasks.get(subtask.getRelationEpicId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
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
            return null;
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epicTasks.get(id) != null) {
            historyManager.addHistoryTask(epicTasks.get(id));
            return epicTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subTasks.get(id) != null) {
            historyManager.addHistoryTask(subTasks.get(id));
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void removeTask(int id) {
        if (tasks.get(id) != null) {
            setIntervalsYearlyTimeTable(listsInterval(tasks.get(id).getStartTime(), tasks.get(id).getEndTime()),
                    false);
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
            setIntervalsYearlyTimeTable(listsInterval(subTasks.get(id).getStartTime(), subTasks.get(id).getEndTime()),
                    false);
            subTasks.remove(id);
            historyManager.removeHistoryTask(id);
            updateEpicTime(epic);
        }
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

                if (subtaskStartTime != null) {
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

    private void updateYearlyTimeTableTask(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null) {
            if (!isConflictTimeIntersection(startTime, endTime)) {
                setIntervalsYearlyTimeTable(listsInterval(startTime, endTime), true);
            } else {
                throw new ManagerTimeIntersectionsException("Task overlap in time. Conflict in period: "
                        + startTime.format(DATE_TIME_FORMATTER) + " - "
                        + endTime.format(DATE_TIME_FORMATTER));
            }
        }
    }

    protected void updateYearlyTimeTableAllTasksAndSubtasks() {
        for (Task task : tasks.values()) {
            updateYearlyTimeTableTask(task.getStartTime(), task.getEndTime());
        }
        for (Subtask subtask : subTasks.values()) {
            updateYearlyTimeTableTask(subtask.getStartTime(), subtask.getEndTime());
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

    private void addTaskInPrioritizedTasks(Task task) {
        updateYearlyTimeTableTask(task.getStartTime(), task.getEndTime());
        prioritizedTasks.add(task);
    }

    private void updateTaskInPrioritizedTasks(Task task, LocalDateTime oldStartTime, LocalDateTime oldEndTime,
                                              Task removeTask) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        List<Integer> oldTimeInterval = listsInterval(oldStartTime, oldEndTime);

        prioritizedTasks.remove(removeTask);
        prioritizedTasks.add(task);

        if (!oldTimeInterval.equals(listsInterval(startTime, endTime))) {
            setIntervalsYearlyTimeTable(oldTimeInterval, false);
            updateYearlyTimeTableTask(startTime, endTime);
        }
    }
}
