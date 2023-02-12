package historyManagerTest;

import historyManagerTest.exception.ManagerGetTaskException;
import model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private File file;

    @BeforeEach
    public void setUp() {
        file = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator
                + "task.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file);
        initTasks();
    }

    @AfterEach
    protected void tearDown() {
        assertTrue(file.delete());
    }

    @Test
    public void loadFromFileEmptyListTask() {
        FileBackedTasksManager tasksManagerEmptyListTask1 = FileBackedTasksManager.loadFromFile(file);
        final List<Task> tasksEmpty1 = tasksManagerEmptyListTask1.getTasks();

        assertNotNull(tasksEmpty1, "Return null list tasks.");
        assertEquals(0, tasksEmpty1.size(), "Return not empty list tasks.");

        tasksManagerEmptyListTask1.addNewTask(task);
        tasksManagerEmptyListTask1.addNewEpic(epic1);
        tasksManagerEmptyListTask1.addNewSubtask(subtask1);
        tasksManagerEmptyListTask1.addNewSubtask(subtask2);

        final List<Task> tasks = tasksManagerEmptyListTask1.getTasks();
        final List<Subtask> subtasks = tasksManagerEmptyListTask1.getSubtasks();
        final List<Epic> epics = tasksManagerEmptyListTask1.getEpics();

        assertNotNull(tasks, "Tasks null");
        assertNotNull(subtasks, "Subtasks null");
        assertNotNull(epics, "Epics null");

        tasksManagerEmptyListTask1.getTask(1);
        tasksManagerEmptyListTask1.getEpic(2);
        tasksManagerEmptyListTask1.getSubtask(3);
        tasksManagerEmptyListTask1.getSubtask(4);

        assertEquals(4, tasksManagerEmptyListTask1.getHistory().size(), "History don't match.");

        tasksManagerEmptyListTask1.deleteAllTasks();
        tasksManagerEmptyListTask1.deleteAllSubtasks();
        tasksManagerEmptyListTask1.deleteAllEpics();

        FileBackedTasksManager tasksManagerEmptyListTask2 = FileBackedTasksManager.loadFromFile(file);
        final List<Task> tasksEmpty2 = tasksManagerEmptyListTask2.getTasks();
        final List<Subtask> subtaskEmpty2 = tasksManagerEmptyListTask2.getSubtasks();
        final List<Epic> epicEmpty2 = tasksManagerEmptyListTask2.getEpics();

        assertEquals(0, tasksManagerEmptyListTask2.getHistory().size(), "History don't match.");

        assertNotNull(tasksEmpty2, "Return null list tasks.");
        assertNotNull(subtaskEmpty2, "Return null list subtasks.");
        assertNotNull(epicEmpty2, "Return null list epics.");

        assertEquals(0, tasksEmpty2.size(), "Return not null list tasks.");
        assertEquals(0, subtaskEmpty2.size(), "Return not null list tasks.");
        assertEquals(0, epicEmpty2.size(), "Return not null list tasks.");
    }

    @Test
    public void loadFromFileEpicWithoutSubtask() {
        taskManager.addNewEpic(epic1);
        final Epic savedEpic = taskManager.getEpic(epic1.getId());
        FileBackedTasksManager tasksManagerEpicWithoutSubtask = FileBackedTasksManager.loadFromFile(file);
        final Epic recoveredEpic = tasksManagerEpicWithoutSubtask.getEpic(epic1.getId());

        assertNotNull(recoveredEpic, "Epics don't recovered.");
        assertEquals(savedEpic, recoveredEpic, "Epics don't match.");

        final ManagerGetTaskException exception = assertThrows(
                ManagerGetTaskException.class,
                () -> {
                    tasksManagerEpicWithoutSubtask.getAllSubTaskForEpic(recoveredEpic.getId());
                });
        assertEquals("Error, Subtasks not found!", exception.getMessage());
    }

    @Test
    public void loadFromFileEmptyListHistory() {
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        final List<Task> listBefore = taskManager.getHistory();

        assertEquals(0, listBefore.size(), "History don't empty.");
        FileBackedTasksManager tasksManagerEmptyListHistory = FileBackedTasksManager.loadFromFile(file);

        assertNotNull(tasksManagerEmptyListHistory.getTasks(), "Tasks don't recovered.");
        assertNotNull(tasksManagerEmptyListHistory.getSubtasks(), "Subtasks don't recovered.");
        assertNotNull(tasksManagerEmptyListHistory.getEpics(), "Epics don't recovered.");

        final List<Task> recoveredList = tasksManagerEmptyListHistory.getHistory();
        assertEquals(0, recoveredList.size(), "History recovered don't empty.");
    }

    @Test
    public void loadFromFileTimeTest() {
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        LocalDateTime oldTask = taskManager.getTask(1).getEndTime();
        LocalDateTime oldEpic1 = taskManager.getEpic(2).getEndTime();
        LocalDateTime oldEpicNull2 = taskManager.getEpic(3).getEndTime();
        LocalDateTime oldSubtask1 = taskManager.getSubtask(4).getEndTime();
        LocalDateTime oldSubtask2 = taskManager.getSubtask(5).getEndTime();

        FileBackedTasksManager tasksManagerTimeTest = FileBackedTasksManager.loadFromFile(file);
        LocalDateTime recoveredTask = tasksManagerTimeTest.getTask(1).getEndTime();
        LocalDateTime recoveredEpic1 = tasksManagerTimeTest.getEpic(2).getEndTime();
        LocalDateTime recoveredEpicNull2 = tasksManagerTimeTest.getEpic(3).getEndTime();
        LocalDateTime recoveredSubtask1 = tasksManagerTimeTest.getSubtask(4).getEndTime();
        LocalDateTime recoveredSubtask2 = tasksManagerTimeTest.getSubtask(5).getEndTime();

        assertNull(oldEpicNull2, "Empty Epic have don't Time null: ");

        assertEquals(oldTask, recoveredTask, "Time don't match in Task: ");
        assertEquals(oldSubtask1, recoveredSubtask1, "Time don't match in Subtask: ");
        assertEquals(oldSubtask2, recoveredSubtask2, "Time don't match in Subtask: ");
        assertEquals(oldEpic1, recoveredEpic1, "Time don't match in Epic: ");
        assertEquals(oldEpicNull2, recoveredEpicNull2, "Time don't match in Epic: ");
    }

}