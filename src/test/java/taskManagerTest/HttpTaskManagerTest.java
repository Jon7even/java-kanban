package taskManagerTest;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskManager;
import service.servers.HttpTaskServer;
import service.servers.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static cfg.config.PORT_KV;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;

    @BeforeEach
    public void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.runServer();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.runServer();
        taskManager = new HttpTaskManager(PORT_KV);
        initTasks();
    }

    @AfterEach
    protected void tearDown() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void endpointTask() {
        taskManager.addNewTask(task);
        Task task2 = new Task(1, TaskType.TASK, "Test Task 2", "Task test description",
                TaskStatus.NEW, 15, LocalDateTime.of(2023, 1, 1, 1, 0));
        Task task3 = new Task(1, TaskType.TASK, "Test Task 3", "Task test description",
                TaskStatus.NEW, 15, LocalDateTime.of(2023, 1, 2, 1, 0));
        int idTask = taskManager.addNewTask(task2); //POST /tasks/task/ Body: {task ..}
        List<Task> listTasks = taskManager.getTasks();
        Task savedTask = taskManager.getTask(idTask);
        HttpTaskManager newHttpTaskManager = taskManager.loadFromHttp();

        List<Task> newListTasks = newHttpTaskManager.getTasks(); //GET /tasks/task
        assertEquals(listTasks.size(), newListTasks.size(), "Get tasks not equal!");

        Task serializedTask = newHttpTaskManager.getTask(idTask); //GET /tasks/task/?id=
        assertEquals(savedTask, serializedTask, "Tasks not equal! /tasks/task/?id=" + idTask);

        int idTaskDelete = newHttpTaskManager.addNewTask(task3);
        newHttpTaskManager.removeTask(idTaskDelete); //DELETE /tasks/task/?id=

        List<Task> listTasksAfterDelete = newHttpTaskManager.getTasks();
        assertEquals(2, listTasksAfterDelete.size(), "Tasks don't Remove");

        newHttpTaskManager.deleteAllTasks(); //DELETE /tasks/task/
        HttpTaskManager newHttpTaskManagerAfterDeleted = newHttpTaskManager.loadFromHttp();
        List<Task> listTasksAfterDeleted = newHttpTaskManagerAfterDeleted.getTasks();
        assertEquals(0, listTasksAfterDeleted.size(), "Tasks don't delete");
    }

    @Test
    public void endpointSubtask() {
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic1);
        final int idSubtask1 = taskManager.addNewSubtask(subtask1); //POST /tasks/subtask/ Body: {subtask ..}
        taskManager.addNewSubtask(subtask2);
        List<Subtask> listSubtasks = taskManager.getSubtasks();
        Subtask subtaskSaved = taskManager.getSubtask(idSubtask1);
        HttpTaskManager newHttpTaskManager = taskManager.loadFromHttp();

        List<Subtask> newListSubtasks = newHttpTaskManager.getSubtasks(); //GET /tasks/subtask
        assertEquals(listSubtasks.size(), newListSubtasks.size(), "Get subtasks not equal!");

        Subtask serializedSubtask = newHttpTaskManager.getSubtask(idSubtask1); //GET /tasks/subtask/?id=
        assertEquals(subtaskSaved, serializedSubtask, "Subtasks not equal! /tasks/subtask/?id=" + idSubtask1);

        int idSubtaskDelete = newHttpTaskManager.addNewSubtask(subtask1);
        newHttpTaskManager.removeSubtask(idSubtaskDelete); //DELETE /tasks/subtask/?id=

        List<Subtask> listSubtasksAfterDelete = newHttpTaskManager.getSubtasks();
        assertEquals(1, listSubtasksAfterDelete.size(), "Subtasks don't Remove");

        newHttpTaskManager.deleteAllSubtasks(); //DELETE /tasks/subtask
        HttpTaskManager newHttpTaskManagerAfterDeleted = newHttpTaskManager.loadFromHttp();
        List<Subtask> listTasksAfterDeleted = newHttpTaskManagerAfterDeleted.getSubtasks();
        assertEquals(0, listTasksAfterDeleted.size(), "Subtasks don't delete");
    }

    @Test
    public void endpointEpic() {
        taskManager.addNewEpic(epic2);
        int idEpic1 = taskManager.addNewEpic(epic1); //POST /tasks/epic/ Body: {subtask ..}
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        List<Epic> listEpics = taskManager.getEpics();
        Epic epicSaved = taskManager.getEpic(idEpic1);
        HttpTaskManager newHttpTaskManager = taskManager.loadFromHttp();

        List<Epic> newListEpics = newHttpTaskManager.getEpics(); //GET /tasks/epic
        assertEquals(listEpics.size(), newListEpics.size(), "Get epics not equal!");

        Epic serializedEpic = newHttpTaskManager.getEpic(idEpic1); //GET /tasks/epic/?id=
        assertEquals(epicSaved, serializedEpic, "Epics not equal! /tasks/epic/?id=" + idEpic1);

        int idEpicDelete = newHttpTaskManager.addNewSubtask(subtask1);
        newHttpTaskManager.removeTask(idEpicDelete); //DELETE /tasks/epic/?id=

        List<Epic> listSubtasksAfterDelete = newHttpTaskManager.getEpics();
        assertEquals(1, listSubtasksAfterDelete.size(), "Epics don't Remove");

        newHttpTaskManager.deleteAllEpics(); //DELETE /tasks/epic
        HttpTaskManager newHttpTaskManagerAfterDeleted = newHttpTaskManager.loadFromHttp();
        List<Epic> listEpicsAfterDeleted = newHttpTaskManagerAfterDeleted.getEpics();
        assertEquals(0, listEpicsAfterDeleted.size(), "Epics don't delete");
    }

    @Test //GET /tasks/subtask/epic/?id=
    public void shouldGetSubtaskByEpicId() {
        taskManager.addNewEpic(epic2);
        final int idEpic = taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        List<Subtask> listAllSubTaskForEpic = taskManager.getAllSubTaskForEpic(idEpic);
        HttpTaskManager newHttpTaskManager = taskManager.loadFromHttp();
        List<Subtask> newListAllSubTaskForEpic = newHttpTaskManager.getAllSubTaskForEpic(idEpic);
        assertEquals(listAllSubTaskForEpic.size(), newListAllSubTaskForEpic.size(), "History tasks not equal!");
    }

    @Test //GET /tasks/history
    public void shouldGetHistory() {
        final int idTask = taskManager.addNewTask(task);
        final int idEpic = taskManager.addNewEpic(epic1);
        final int idSubtask1 = taskManager.addNewSubtask(subtask1);
        final int idSubtask2 = taskManager.addNewSubtask(subtask2);
        taskManager.getTask(idTask);
        taskManager.getSubtask(idSubtask2);
        taskManager.getEpic(idEpic);
        taskManager.getSubtask(idSubtask1);
        List<Task> getHistory = taskManager.getHistory();
        HttpTaskManager newHttpTaskManager = taskManager.loadFromHttp();
        List<Task> newGetHistory = newHttpTaskManager.getHistory();
        assertEquals(getHistory.size(), newGetHistory.size(), "History tasks not equal!");
    }

    @Test //GET /tasks/
    public void shouldGetPrioritizedTasks() {
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        final TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.size(), "Priority tasks are empty!");
        HttpTaskManager newHttpTaskManager = taskManager.loadFromHttp();
        final TreeSet<Task> newPrioritizedTasks = newHttpTaskManager.getPrioritizedTasks();
        assertEquals(prioritizedTasks.size(), newPrioritizedTasks.size(), "Priority tasks not equal!");
    }
}
