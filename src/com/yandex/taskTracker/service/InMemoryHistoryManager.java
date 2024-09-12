package com.yandex.taskTracker.service;

import com.yandex.taskTracker.model.Task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_SIZE_HISTORY = 10;
    private final List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= MAX_SIZE_HISTORY){
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory(){
        return new LinkedList<>(history);
    }

}
