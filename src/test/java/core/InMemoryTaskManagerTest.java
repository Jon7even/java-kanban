package test.java.core;

import main.java.core.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        initTasks();
    }

    @Test
    public void createInMemoryTaskManager() {
        assertNotNull(taskManager.getTasks(), "Return null list Tasks");
    }

}