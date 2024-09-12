package com.yandex.taskTracker.service;

import com.yandex.taskTracker.model.Task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    final private static int maxSizeHistory = 10;
    private List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= maxSizeHistory){
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory(){
        return new LinkedList<>(history);
    }

}
