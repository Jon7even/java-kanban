package core;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    public InMemoryHistoryManager() {
    }

    private static class Node {
        public Node prev;
        public Task task;
        public Node next;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }

    }

    private final HashMap<Integer, Node> nodeMap = new HashMap<>();
    public Node head;
    public Node tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        if (nodeMap.isEmpty()) {
            return null;
        } else {
            tasks.add(head.task);
            Node nextNode = head;
            while (nextNode.next != null) {
                nextNode = nextNode.next;
                tasks.add(nextNode.task);
            }
        }
        return tasks;
    }

    @Override
    public void addHistoryTask(Task task) {
        int taskId = task.getId();

        if (nodeMap.containsKey(taskId)) {
            removeHistoryTask(taskId);
        }
        nodeMap.put(taskId, linkLast(task));
    }

    private Node linkLast(Task task) {
        Node oldTail = tail;
        Node node = new Node(tail, task, null);
        tail = node;
        if (oldTail == null) {
            head = node;
        } else {
            oldTail.next = node;
        }
        return node;
    }

    @Override
    public void removeHistoryTask(int id) {
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
            nodeMap.remove(id);
        }
    }

    private void removeNode(Node node) {
        if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else if (node.prev == null && node.next == null) {
            tail = null;
        } else if (node.prev == null) {
            head = node.next;
            node.next.prev = null;
        } else {
            tail = node.prev;
            node.prev.next = null;
        }

    }

}