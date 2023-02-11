package test.java.core;

import main.java.core.TaskManager;
import main.java.core.exception.*;
import main.java.tasks.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic1;
    protected Epic epic2;
    protected Subtask subtask1;
    protected Subtask subtask2;

    protected void initTasks() {
        task = new Task(1, TaskType.TASK, "Test Task", "Task test description",
                TaskStatus.NEW, 15, LocalDateTime.of(2023, 1, 1, 0, 0));

        epic1 = new Epic(2, TaskType.EPIC, "Test Epic 1", "Epic 1 test description",
                TaskStatus.NEW);

        epic2 = new Epic(3, TaskType.EPIC, "Test Epic 2", "Epic 2 test description",
                TaskStatus.NEW);

        subtask1 = new Subtask(4, TaskType.SUBTASK, "Test Subtask 1", "Subtask 1 test description",
                TaskStatus.NEW, 15, LocalDateTime.of(2023, 1, 1, 0, 30),
                epic1.getId());

        subtask2 = new Subtask(5, TaskType.SUBTASK, "Test Subtask 1", "Subtask 2 test description",
                TaskStatus.NEW, 15, LocalDateTime.of(2023, 1, 1, 1, 0),
                epic1.getId());
    }

    @Test
    public void addNewTask() {
        Task taskNew = new Task(TaskType.TASK, "Test Task", "Task test description",
                TaskStatus.NEW, 15, LocalDateTime.of(2023, 1, 1, 1, 17));
        final int taskId = taskManager.addNewTask(taskNew);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Task not found.");
        assertEquals(taskNew, savedTask, "Tasks don't match.");

        taskManager.addNewTask(task);
        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Tasks don't return");
        assertEquals(2, tasks.size(), "Tasks don't match");
        assertEquals(taskNew, tasks.get(0), "Tasks don't match");

        final ManagerAddTaskException exception = assertThrows(
                ManagerAddTaskException.class,
                () -> {
                    taskManager.addNewTask(null);
                });
        assertEquals("Error, Task cannot be passed: ", exception.getMessage());
    }

    @Test
    public void addNewEpic() {
        assertNotNull(taskManager.getEpics(), "Return null list Epics");
        Epic epicNew = new Epic(TaskType.EPIC, "Test Epic", "Epic test description", TaskStatus.NEW);
        final int epicId = taskManager.addNewEpic(epicNew);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Epics not found.");
        assertEquals(epicNew, savedEpic, "Epics don't match.");

        taskManager.addNewEpic(epic1);
        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Epics don't return");
        assertEquals(2, epics.size(), "Epics don't match");
        assertEquals(epicNew, epics.get(0), "Epics don't match");

        final ManagerAddTaskException exception = assertThrows(
                ManagerAddTaskException.class,
                () -> {
                    taskManager.addNewEpic(null);
                });
        assertEquals("Error, Epic cannot be passed: ", exception.getMessage());
    }

    @Test
    public void addNewSubtask() {
        assertNotNull(taskManager.getSubtasks(), "Return null list Subtasks");

        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic1);
        Subtask subtaskNew = subtask1;
        int idSubtask = taskManager.addNewSubtask(subtaskNew);

        final Subtask savedSubtask = taskManager.getSubtask(idSubtask);

        assertNotNull(savedSubtask, "Subtask null.");
        assertEquals(subtaskNew, savedSubtask, "Subtask don't match.");

        final List<Subtask> subtask = taskManager.getSubtasks();

        assertNotNull(subtask, "Subtasks don't return");
        assertEquals(1, subtask.size(), "Subtask don't match");
        assertEquals(subtaskNew, subtask.get(0), "Subtask don't match");

        final ManagerAddTaskException exception = assertThrows(
                ManagerAddTaskException.class,
                () -> {
                    taskManager.addNewSubtask(null);
                });
        assertEquals("Error, Subtask cannot be passed: ", exception.getMessage());
    }

    @Test
    public void getAllSubTaskForEpic() {
        Epic epicNew = new Epic(TaskType.EPIC, "Test Epic", "Epic test description", TaskStatus.NEW);
        final int epicId = taskManager.addNewEpic(epicNew);
        taskManager.addNewEpic(epicNew);

        final ManagerGetTaskException exceptionEpicNotSubtask = assertThrows(
                ManagerGetTaskException.class,
                () -> {
                    taskManager.getAllSubTaskForEpic(epicId);
                });
        assertEquals("Error, Subtasks not found!", exceptionEpicNotSubtask.getMessage());

        Subtask subtaskNew1 = new Subtask(TaskType.SUBTASK, "Test Subtask",
                "Subtask test description", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 1, 1, 1, 0), 1);
        taskManager.addNewSubtask(subtaskNew1);

        List<Subtask> subtasksSizeOne = taskManager.getAllSubTaskForEpic(epicId);
        assertEquals(subtaskNew1, subtasksSizeOne.get(0), "Subtask don't match");

        Subtask subtaskNew2 = new Subtask(TaskType.SUBTASK, "Test Subtask",
                "Subtask test description", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 1, 1, 1, 30), 1);
        taskManager.addNewSubtask(subtaskNew2);

        List<Subtask> subtasksSizeTwo = taskManager.getAllSubTaskForEpic(epicId);
        assertEquals(2, subtasksSizeTwo.size(), "Subtask don't match");

        final ManagerGetTaskException exceptionEpicNull = assertThrows(
                ManagerGetTaskException.class,
                () -> {
                    taskManager.getAllSubTaskForEpic(3);
                });
        assertEquals("Error, Epic with id 3 cannot be received!", exceptionEpicNull.getMessage());
    }

    @Test
    public void updateTask() {
        Task firstTask = task;
        final int idNewTask = taskManager.addNewTask(firstTask);

        Task taskUpdate = taskManager.getTask(idNewTask);
        taskUpdate.setStatus(TaskStatus.IN_PROGRESS);
        taskUpdate.setDescription("Update");
        taskUpdate.setDuration(30);
        taskManager.updateTask(taskUpdate);
        Task taskGetUpdate = taskManager.getTask(idNewTask);

        assertEquals(taskUpdate, taskGetUpdate, "Tasks don't match");

        final ManagerAddTaskException exceptionAddTaskNull = assertThrows(
                ManagerAddTaskException.class,
                () -> {
                    taskManager.updateTask(null);
                });
        assertEquals("Error, Task cannot be passed: ", exceptionAddTaskNull.getMessage());
    }

    @Test
    public void updateSubtask() {
        Epic epic = epic1;
        taskManager.addNewEpic(epic);
        Subtask subtaskOne = new Subtask(TaskType.SUBTASK, "Test Subtask",
                "Subtask test description", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 1, 1, 1, 30), epic.getId());
        final int idNewSubtask = taskManager.addNewSubtask(subtaskOne);

        Subtask subtaskUpdate = taskManager.getSubtask(idNewSubtask);
        subtaskUpdate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskUpdate.setDescription("Update");
        subtaskUpdate.setDuration(15);
        subtaskUpdate.setStartTime(LocalDateTime.now());
        taskManager.updateSubtask(subtaskUpdate);
        Subtask subtaskGetUpdate = taskManager.getSubtask(idNewSubtask);

        assertEquals(subtaskUpdate, subtaskGetUpdate, "Subtasks don't match");

        final ManagerAddTaskException exceptionAddSubtaskNull = assertThrows(
                ManagerAddTaskException.class,
                () -> {
                    taskManager.updateSubtask(null);
                });
        assertEquals("Error, Subtask cannot be passed: ", exceptionAddSubtaskNull.getMessage());
    }

    @Test
    public void updateEpic() {
        Epic epic = epic1;
        final int idNewEpic = taskManager.addNewEpic(epic);

        Epic epicUpdate = taskManager.getEpic(idNewEpic);
        epicUpdate.setDescription("Update");
        epicUpdate.setName("Epic Update");
        taskManager.updateEpic(epicUpdate);
        Epic epicGetUpdate = taskManager.getEpic(idNewEpic);

        assertEquals(epicUpdate, epicGetUpdate, "Subtasks don't match");

        final ManagerAddTaskException exceptionAddEpicNull = assertThrows(
                ManagerAddTaskException.class,
                () -> {
                    taskManager.updateEpic(null);
                });
        assertEquals("Error, Epic cannot be passed: ", exceptionAddEpicNull.getMessage());
    }

    @Test
    public void deleteAllTasks() {
        List<Task> emptyTasks = taskManager.getTasks();
        assertEquals(0, emptyTasks.size(), "Tasks list don't empty.");

        taskManager.addNewTask(task);
        Task taskNew = new Task(TaskType.TASK, "Test Task", "Task test description",
                TaskStatus.NEW, 15, LocalDateTime.of(2023, 1, 1, 1, 17));
        taskManager.addNewTask(taskNew);

        taskManager.deleteAllTasks();

        List<Task> emptyAfterDeleteAllTask = taskManager.getTasks();
        assertEquals(0, emptyAfterDeleteAllTask.size(), "Tasks list don't empty.");
    }

    @Test
    public void deleteAllSubtasks() {
        List<Subtask> emptySubtasks = taskManager.getSubtasks();
        assertEquals(0, emptySubtasks.size(), "Subtasks list don't empty.");
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteAllSubtasks();

        List<Subtask> emptyAfterDeleteAllSubtask = taskManager.getSubtasks();
        assertEquals(0, emptyAfterDeleteAllSubtask.size(), "Subtasks list don't empty.");
    }

    @Test
    public void deleteAllEpics() {
        List<Epic> emptyEpics = taskManager.getEpics();
        assertEquals(0, emptyEpics.size(), "Epics list don't empty.");
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteAllSubtasks();

        List<Subtask> emptyAfterDeleteAllEpics = taskManager.getSubtasks();
        assertEquals(0, emptyAfterDeleteAllEpics.size(), "Epics list don't empty.");
    }

    @Test
    public void removeTask() {
        int idDeleteTask = taskManager.addNewTask(task);

        taskManager.removeTask(idDeleteTask);

        List<Task> emptyTasks = taskManager.getTasks();
        assertEquals(0, emptyTasks.size(), "Tasks list don't empty.");

        final ManagerRemoveTaskException exceptionRemoveTask = assertThrows(
                ManagerRemoveTaskException.class,
                () -> {
                    taskManager.removeTask(2);
                });
        assertEquals("Error, Task with id 2 cannot be deleted!", exceptionRemoveTask.getMessage());
    }

    @Test
    public void removeSubtask() {
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic1);
        int idDeleteSubtask = taskManager.addNewSubtask(subtask1);

        taskManager.removeSubtask(idDeleteSubtask);

        List<Subtask> emptySubtasks = taskManager.getSubtasks();
        assertEquals(0, emptySubtasks.size(), "Subtasks list don't empty.");

        final ManagerRemoveTaskException exceptionRemoveSubtask = assertThrows(
                ManagerRemoveTaskException.class,
                () -> {
                    taskManager.removeSubtask(5);
                });
        assertEquals("Error, Subtask with id 5 cannot be deleted!", exceptionRemoveSubtask.getMessage());
    }

    @Test
    public void removeEpic() {
        int idDeleteEpic = taskManager.addNewEpic(epic1);

        taskManager.removeEpic(idDeleteEpic);

        List<Epic> emptyEpics = taskManager.getEpics();
        assertEquals(0, emptyEpics.size(), "Epics list don't empty.");

        final ManagerRemoveTaskException exceptionRemoveEpics = assertThrows(
                ManagerRemoveTaskException.class,
                () -> {
                    taskManager.removeEpic(2);
                });
        assertEquals("Error, Epic with id 2 cannot be deleted!", exceptionRemoveEpics.getMessage());
    }

    @Test
    public void getTask() {
        int idAddTask = taskManager.addNewTask(task);
        Task getTask = taskManager.getTask(idAddTask);

        assertNotNull(getTask, "Task Null!");

        final ManagerGetTaskException exceptionAddTask = assertThrows(
                ManagerGetTaskException.class,
                () -> {
                    taskManager.getTask(2);
                });
        assertEquals("Error, Task with id 2 cannot be received!", exceptionAddTask.getMessage());
    }

    @Test
    public void getEpic() {
        int idAddEpic = taskManager.addNewEpic(epic1);
        Epic getEpic = taskManager.getEpic(idAddEpic);

        assertNotNull(getEpic, "Epic Null!");

        final ManagerGetTaskException exceptionAddEpic = assertThrows(
                ManagerGetTaskException.class,
                () -> {
                    taskManager.getEpic(2);
                });
        assertEquals("Error, Epic with id 2 cannot be received!", exceptionAddEpic.getMessage());
    }

    @Test
    public void getSubtask() {
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic1);
        int idAddSubtask = taskManager.addNewSubtask(subtask1);
        Subtask getSubtask = taskManager.getSubtask(idAddSubtask);

        assertNotNull(getSubtask, "Subtask Null!");

        final ManagerGetTaskException exceptionAddSubtask = assertThrows(
                ManagerGetTaskException.class,
                () -> {
                    taskManager.getSubtask(2);
                });
        assertEquals("Error, Subtask with id 2 cannot be received!", exceptionAddSubtask.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenAddSubtaskWrongEpicId() {
        taskManager.addNewEpic(epic1);
        Subtask subtaskWrongEpicId = new Subtask(TaskType.SUBTASK, "Test Subtask",
                "Subtask test description", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 1, 1, 2, 0), 2);

        final ManagerGetTaskException exceptionEpicDontExist = assertThrows(
                ManagerGetTaskException.class,
                () -> {
                    taskManager.addNewSubtask(subtaskWrongEpicId);
                });
        assertEquals("Error, Epic cannot be received!", exceptionEpicDontExist.getMessage());
    }

    @Test
    public void shouldEpicStatusBeNewWhenSubtaskEmpty() {
        int idAddEpic = taskManager.addNewEpic(epic1);
        Epic getEpic = taskManager.getEpic(idAddEpic);
        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Epic Status don't match.");
    }

    @Test
    public void shouldEpicStatusBeNewWhenSubtaskAllHaveStatusNew() {
        taskManager.addNewEpic(epic1);
        int idAddEpic = taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        Epic getEpic = taskManager.getEpic(idAddEpic);
        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Epic Status don't match.");
    }

    @Test
    public void shouldEpicStatusBeDoneWhenSubtaskHaveAllStatusDone() {
        taskManager.addNewEpic(epic1);
        int idAddEpic = taskManager.addNewEpic(epic2);
        int idSubtask1 = taskManager.addNewSubtask(subtask1);
        int idSubtask2 = taskManager.addNewSubtask(subtask2);

        Subtask subtaskEdit1 = taskManager.getSubtask(idSubtask1);
        subtaskEdit1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtaskEdit1);

        Subtask subtaskEdit2 = taskManager.getSubtask(idSubtask2);
        subtaskEdit2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtaskEdit2);

        Epic getEpic = taskManager.getEpic(idAddEpic);
        assertEquals(TaskStatus.DONE, getEpic.getStatus(), "Epic Status don't match.");
    }

    @Test
    public void shouldEpicStatusBeInProgressWhenSubtaskHaveStatusDoneAndNew() {
        taskManager.addNewEpic(epic1);
        int idAddEpic = taskManager.addNewEpic(epic2);
        int idSubtask = taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        Subtask subtaskEdit = taskManager.getSubtask(idSubtask);
        subtaskEdit.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtaskEdit);

        Epic getEpic = taskManager.getEpic(idAddEpic);
        assertEquals(TaskStatus.IN_PROGRESS, getEpic.getStatus(), "Epic Status don't match.");
    }

    @Test
    public void shouldEpicStatusBeInProgressWhenSubtaskHaveStatusProgress() {
        taskManager.addNewEpic(epic1);
        int idAddEpic = taskManager.addNewEpic(epic2);
        int idSubtask1 = taskManager.addNewSubtask(subtask1);
        int idSubtask2 = taskManager.addNewSubtask(subtask2);

        Subtask subtaskEdit1 = taskManager.getSubtask(idSubtask1);
        subtaskEdit1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtaskEdit1);

        Subtask subtaskEdit2 = taskManager.getSubtask(idSubtask2);
        subtaskEdit2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtaskEdit2);

        Epic getEpic = taskManager.getEpic(idAddEpic);
        assertEquals(TaskStatus.IN_PROGRESS, getEpic.getStatus(), "Epic Status don't match.");
    }

    @Test
    public void shouldBeNullStartTimeTask() {
        Task taskNew = new Task(TaskType.TASK, "Test Task", "Task test description",
                TaskStatus.NEW, 0, null);
        int idTask = taskManager.addNewTask(taskNew);
        assertNull(taskManager.getTask(idTask).getStartTime(), "Time don't null");
    }

    @Test
    public void shouldBeNullStartTimeSubtask() {
        taskManager.addNewEpic(epic2);
        int idEpic = taskManager.addNewEpic(epic1);
        Subtask SubtaskNew = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 0, null, idEpic);
        int idSubtask = taskManager.addNewSubtask(SubtaskNew);
        assertNull(taskManager.getSubtask(idSubtask).getStartTime(), "Time don't null");
    }

    @Test
    public void shouldBeNullStartTimeEpic() {
        int idEpic = taskManager.addNewEpic(epic1);
        assertNull(taskManager.getEpic(idEpic).getStartTime(), "Time don't null");
    }

    @Test
    public void shouldBeGetEndTimeTask() {
        LocalDateTime firstTime = LocalDateTime.of(2023, 1, 1, 0, 0);

        Task taskNew = new Task(TaskType.TASK, "Test Task", "Task test description",
                TaskStatus.NEW, 15, firstTime);
        int idTask = taskManager.addNewTask(taskNew);
        Task taskWithTime = taskManager.getTask(idTask);

        assertNotNull(taskWithTime.getEndTime(), "EndTime null.");
        assertEquals(firstTime.plusMinutes(15), taskWithTime.getEndTime(), "Time don't match.");
    }

    @Test
    public void shouldBeGetEndTimeSubtask() {
        taskManager.addNewEpic(epic2);
        int idEpic = taskManager.addNewEpic(epic1);
        LocalDateTime firstTime = LocalDateTime.of(2023, 1, 1, 0, 0);

        Subtask SubtaskNew = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 15, firstTime, idEpic);

        int idSubtask = taskManager.addNewSubtask(SubtaskNew);
        Subtask subtaskWithTime = taskManager.getSubtask(idSubtask);

        assertNotNull(subtaskWithTime.getEndTime(), "EndTime null.");
        assertEquals(firstTime.plusMinutes(15), subtaskWithTime.getEndTime(), "Time don't match.");
    }

    @Test
    public void shouldBeGetStartTimeAndEndTimeEpicWhenOneSubtask() {
        taskManager.addNewEpic(epic2);
        int idEpic = taskManager.addNewEpic(epic1);
        LocalDateTime firstTime = LocalDateTime.of(2023, 2, 1, 0, 0);

        Subtask SubtaskNew = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 15, firstTime, idEpic);

        int idSubtask = taskManager.addNewSubtask(SubtaskNew);
        Subtask subtaskWithTime = taskManager.getSubtask(idSubtask);
        Epic epicWithTime = taskManager.getEpic(idEpic);

        assertNotNull(epicWithTime.getEndTime(), "EndTime null.");
        assertNotNull(epicWithTime.getStartTime(), "StartTime null.");
        assertEquals(firstTime.plusMinutes(15), epicWithTime.getEndTime(), "Time don't match.");
        assertEquals(subtaskWithTime.getStartTime(), epicWithTime.getStartTime(), "Time don't match.");
    }

    @Test
    public void shouldBeGetStartTimeAndEndTimeEpicWhenTwoSubtask() {
        taskManager.addNewEpic(epic2);
        int idEpic = taskManager.addNewEpic(epic1);
        LocalDateTime firstTime = LocalDateTime.of(2023, 2, 1, 0, 0);

        Subtask SubtaskNew1 = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 14, firstTime, idEpic);
        int idSubtask1 = taskManager.addNewSubtask(SubtaskNew1);
        Subtask subtaskWithTime1 = taskManager.getSubtask(idSubtask1);

        Subtask SubtaskNew2 = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 15, firstTime.plusMinutes(15), idEpic);
        int idSubtask2 = taskManager.addNewSubtask(SubtaskNew2);
        Subtask subtaskWithTime2 = taskManager.getSubtask(idSubtask2);

        Epic epicWithTime = taskManager.getEpic(idEpic);

        assertNotNull(epicWithTime.getEndTime(), "EndTime null.");
        assertNotNull(epicWithTime.getStartTime(), "StartTime null.");
        assertEquals(subtaskWithTime2.getEndTime(), epicWithTime.getEndTime(), "Time don't match.");
        assertEquals(subtaskWithTime1.getStartTime(), epicWithTime.getStartTime(), "Time don't match.");
    }

    @Test
    public void shouldBeGetStartTimeAndEndTimeEpicWhenTwoSubtaskUpdateTimeBackward() {
        taskManager.addNewEpic(epic2);
        int idEpic = taskManager.addNewEpic(epic1);
        LocalDateTime firstTime = LocalDateTime.of(2023, 2, 1, 0, 0);

        Subtask SubtaskNew1 = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 14, firstTime, idEpic);
        int idSubtask1 = taskManager.addNewSubtask(SubtaskNew1);
        taskManager.getSubtask(idSubtask1);

        Subtask SubtaskNew2 = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 15, firstTime.plusMinutes(15), idEpic);
        int idSubtask2 = taskManager.addNewSubtask(SubtaskNew2);

        Subtask subtaskUpdateBackwardTime = taskManager.getSubtask(idSubtask1);
        subtaskUpdateBackwardTime.setStartTime(firstTime.minusMinutes(15));
        taskManager.updateSubtask(subtaskUpdateBackwardTime);

        Epic epicWithTime = taskManager.getEpic(idEpic);
        Subtask subtaskAfterUpdate1 = taskManager.getSubtask(idSubtask1);
        Subtask subtaskWithTime2 = taskManager.getSubtask(idSubtask2);

        assertNotNull(epicWithTime.getEndTime(), "EndTime null.");
        assertNotNull(epicWithTime.getStartTime(), "StartTime null.");
        assertEquals(subtaskWithTime2.getEndTime(), epicWithTime.getEndTime(), "Time don't match.");
        assertEquals(subtaskAfterUpdate1.getStartTime(), epicWithTime.getStartTime(), "Time don't match.");
    }

    @Test
    public void shouldBeGetStartTimeAndEndTimeEpicWhenTwoSubtaskUpdateTimeForward() {
        taskManager.addNewEpic(epic2);
        int idEpic = taskManager.addNewEpic(epic1);
        LocalDateTime firstTime = LocalDateTime.of(2023, 2, 1, 0, 0);

        Subtask SubtaskNew1 = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 14, firstTime, idEpic);
        int idSubtask1 = taskManager.addNewSubtask(SubtaskNew1);
        taskManager.getSubtask(idSubtask1);

        Subtask SubtaskNew2 = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 15, firstTime.plusMinutes(15), idEpic);
        int idSubtask2 = taskManager.addNewSubtask(SubtaskNew2);

        Subtask subtaskUpdateForwardTime = taskManager.getSubtask(idSubtask2);
        subtaskUpdateForwardTime.setStartTime(firstTime.plusMinutes(45));
        taskManager.updateSubtask(subtaskUpdateForwardTime);

        Epic epicWithTime = taskManager.getEpic(idEpic);
        Subtask subtaskWithTime1 = taskManager.getSubtask(idSubtask1);
        Subtask subtaskAfterUpdate2 = taskManager.getSubtask(idSubtask2);

        assertNotNull(epicWithTime.getEndTime(), "EndTime null.");
        assertNotNull(epicWithTime.getStartTime(), "StartTime null.");
        assertEquals(subtaskAfterUpdate2.getEndTime(), epicWithTime.getEndTime(), "Time don't match.");
        assertEquals(subtaskWithTime1.getStartTime(), epicWithTime.getStartTime(), "Time don't match.");
    }

    @Test
    public void shouldThrowExceptionTimeWhenAddSubtaskForBusyTime() {
        taskManager.addNewEpic(epic2);
        int idEpic = taskManager.addNewEpic(epic1);
        LocalDateTime firstTime = LocalDateTime.of(2023, 1, 1, 0, 5);

        Subtask SubtaskNew = new Subtask(TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 15, firstTime, idEpic);
        taskManager.addNewSubtask(SubtaskNew);

        final ManagerTimeIntersectionsException exceptionSubtaskBusyTime = assertThrows(
                ManagerTimeIntersectionsException.class,
                () -> {
                    taskManager.addNewTask(task);
                });
        assertEquals("Task overlap in time. Task id - 1\n    "
                + "Conflict in period: 00:00 01.01.23 - 00:15 01.01.23", exceptionSubtaskBusyTime.getMessage());
    }

}
