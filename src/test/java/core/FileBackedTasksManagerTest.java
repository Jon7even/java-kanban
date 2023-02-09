package test.java.core;

import main.java.core.FileBackedTasksManager;
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
    }

    @AfterEach
    protected void tearDown() {
        assertTrue(file.delete());
    }

    @Test
    public void loadFromFile() {
        FileBackedTasksManager tasksManager2 = FileBackedTasksManager.loadFromFile(file);
        final List<Task> tasks = tasksManager2.getTasks();
        assertNotNull(tasks, "Return not null list tasks");
        assertEquals(1, tasks.size(), "Return not null list tasks");

    }

}