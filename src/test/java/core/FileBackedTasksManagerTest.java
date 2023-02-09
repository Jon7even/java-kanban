package test.java.core;

import main.java.core.FileBackedTasksManager;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
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
        /*taskManager.updateTask(task);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);*/
    }

    @AfterEach
    protected void tearDown() {
        assertTrue(file.delete());
    }

    @Test
    public void loadFromFileEpicWithoutSubtask() {
        taskManager.addNewEpic(epic1);
        final Epic savedEpic = taskManager.getEpic(epic1.getId());
        FileBackedTasksManager tasksManagerEpicWithoutSubtask = FileBackedTasksManager.loadFromFile(file);
        final Epic recoveredEpic = tasksManagerEpicWithoutSubtask.getEpic(epic1.getId());
        assertEquals(savedEpic, recoveredEpic, "Epics don't match.");

        List<Subtask> subtasksBefore = taskManager.getAllSubTaskForEpic(savedEpic.getId());
        List<Subtask> subtasksBefore = taskManager.getAllSubTaskForEpic(savedEpic.getId());
        assertEquals(savedEpic, recoveredEpic, "Epics don't match.");
    }
/*
    @Test
    public void loadFromFile() {
        FileBackedTasksManager tasksManagerFileBacked = FileBackedTasksManager.loadFromFile(file);
        final List<Task> tasks = tasksManagerFileBacked.getTasks();
        assertNotNull(tasks, "Return not null list tasks");
        assertEquals(0, tasks.size(), "Return not null list tasks");
    }*/

}