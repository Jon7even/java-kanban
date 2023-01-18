package core;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;

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

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task + "\n" +
                    '}';
        }
    }

    private final HashMap<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node nextNode = head;
        while (nextNode != null) {
            tasks.add(nextNode.task);
            nextNode = nextNode.next;
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
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return nodeMap.equals(that.nodeMap) && head.equals(that.head) && tail.equals(that.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeMap, head, tail);
    }

    @Override
    public String toString() {
        return "HistoryManager{" +
                "nodeMap=" + nodeMap +
                '}' + "\n";
    }
}