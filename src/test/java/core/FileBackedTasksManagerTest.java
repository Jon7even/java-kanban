package test.java.core;

import main.java.core.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private File file;

    @BeforeEach
    public void setUp() {
        file = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator
                + "task.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file);
        initTasks();
    }

}