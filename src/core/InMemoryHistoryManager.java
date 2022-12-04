package core;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> tasks = new ArrayList<>() ;

    @Override
    public List<Task> getHistory() {
       return tasks;
    }

    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }

}
