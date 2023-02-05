package test.java.core;

import main.java.core.HistoryManager;
import main.java.core.InMemoryHistoryManager;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }
}