package com.yandex.taskTracker.service;

import com.yandex.taskTracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

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

    private void linkLast(Task task){
        final Node oldTail = tail;
        final Node newNode = new Node(tail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        }
        else {
            oldTail.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    private void removeNode(Node node){
        if(node == null){
            return;
        }

        if(node.prev == null){
            head = node.next;
            node.next.prev = null;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }

        if (node.next == null){
            tail = node.prev;
            node.prev.next = null;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
    }
    @Override
    public void add(Task task){
        if(history.containsKey(task.getId())){
            removeNode(history.get(task.getId()));
        }
        linkLast(task);
    }

    @Override
    public void remove(int id){
        history.remove(id);

    }

    @Override
    public List<Task> getHistory(){
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null){
            tasks.add(current.item);
            current = current.next;
        }
        return tasks;
    }
}
