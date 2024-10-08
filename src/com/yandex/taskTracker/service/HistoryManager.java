package com.yandex.taskTracker.service;
import com.yandex.taskTracker.model.*;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}