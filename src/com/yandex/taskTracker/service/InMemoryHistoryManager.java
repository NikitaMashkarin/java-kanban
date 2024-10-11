package com.yandex.taskTracker.service;

import com.yandex.taskTracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node head;
    private Node tail;
    private final Map<Integer, Node> history = new HashMap<>();

    @Override
    public void add(Task task) {
        removeNode(history.get(task.getId()));
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(history.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>(history.size());
        Node current = head;
        while (current != null) {
            tasks.add(current.item);
            current = current.next;
        }
        return tasks;
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(tail, task, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        history.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
        }

        node.prev = null;
        node.next = null;
    }
}