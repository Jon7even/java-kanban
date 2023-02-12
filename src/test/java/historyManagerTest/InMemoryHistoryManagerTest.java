package service;

import service.exception.HistoryManagerAddTask;
import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();

        task = new Task(1, TaskType.TASK, "Test Task", "Task test description",
                TaskStatus.NEW, 15, LocalDateTime.now());
        epic = new Epic(2, TaskType.EPIC, "Test Epic", "Epic test description",
                TaskStatus.NEW);
        subtask = new Subtask(3, TaskType.SUBTASK, "Test Subtask", "Subtask test description",
                TaskStatus.NEW, 30, task.getEndTime(), epic.getId());
    }

    @Test
    public void getHistory() {
        assertNotNull(historyManager.getHistory(), "History null.");
    }

    @Test
    public void shouldThrowExceptionAddNullTaskInHistory() {
        final HistoryManagerAddTask exception = assertThrows(
                HistoryManagerAddTask.class,
                () -> {
                    historyManager.addHistoryTask(null);
                });
        assertEquals("Task cannot be null: ", exception.getMessage());
    }

    @Test
    public void removeHistoryTask() {
        historyManager.addHistoryTask(task);
        historyManager.addHistoryTask(epic);
        historyManager.addHistoryTask(subtask);
        historyManager.removeHistoryTask(3);
        historyManager.removeHistoryTask(2);
        historyManager.removeHistoryTask(1);
        assertEquals(0, historyManager.getHistory().size(), "Task was not removed in history.");
        historyManager.removeHistoryTask(4);
    }

    @Test
    public void shouldAddOneTaskInHistory() {
        historyManager.addHistoryTask(task);
        assertEquals(1, historyManager.getHistory().size(), "Task was not added in history.");
    }

    @Test
    public void shouldDontMissOneDuplicateTaskInHistory() {
        historyManager.addHistoryTask(task);
        historyManager.addHistoryTask(task);
        assertEquals(1, historyManager.getHistory().size(), "History Tasks have Duplicate.");
    }

    @Test
    public void shouldDontMissTwiceDuplicateTaskInHistory() {
        historyManager.addHistoryTask(task);
        historyManager.addHistoryTask(subtask);
        historyManager.addHistoryTask(task);
        historyManager.addHistoryTask(task);
        assertEquals(2, historyManager.getHistory().size(), "History Tasks have Duplicate.");
    }

    @Test
    public void shouldRemoveTaskInHeadHistory() {
        historyManager.addHistoryTask(task);
        historyManager.addHistoryTask(epic);
        historyManager.addHistoryTask(subtask);
        historyManager.removeHistoryTask(1);
        List<Task> idExpected = new ArrayList<>();
        idExpected.add(epic);
        idExpected.add(subtask);
        assertArrayEquals(idExpected.toArray(), historyManager.getHistory().toArray(),
                "Error removing task in head.");
    }

    @Test
    public void shouldRemoveTaskInMiddleHistory() {
        historyManager.addHistoryTask(task);
        historyManager.addHistoryTask(epic);
        historyManager.addHistoryTask(subtask);
        historyManager.removeHistoryTask(2);
        List<Task> idExpected = new ArrayList<>();
        idExpected.add(task);
        idExpected.add(subtask);
        assertArrayEquals(idExpected.toArray(), historyManager.getHistory().toArray(),
                "Error removing task in middle.");
    }

    @Test
    public void shouldRemoveTaskInTailHistory() {
        historyManager.addHistoryTask(task);
        historyManager.addHistoryTask(epic);
        historyManager.addHistoryTask(subtask);
        historyManager.removeHistoryTask(3);
        List<Task> idExpected = new ArrayList<>();
        idExpected.add(task);
        idExpected.add(epic);
        assertArrayEquals(idExpected.toArray(), historyManager.getHistory().toArray(),
                "Task was not removed in history.");
    }

}