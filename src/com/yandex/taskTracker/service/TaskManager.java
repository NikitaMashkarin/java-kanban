package com.yandex.taskTracker.service;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.model.Task;

import java.util.*;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtask();

    Set<Task> getPrioritizedTasks();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    List<Subtask> getSubtaskCertainEpic(int id);

    List<Task> getHistory();
}
