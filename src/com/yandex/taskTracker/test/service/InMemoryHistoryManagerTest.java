package com.yandex.taskTracker.test.service;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.service.HistoryManager;
import com.yandex.taskTracker.service.Managers;
import com.yandex.taskTracker.service.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    public void tasksAddedToTheHistoryManagerRetainItsPreviousVersion() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Name", "Description");
        taskManager.addTask(task);
        taskManager.getTaskById(1);
        Task task1 = new Task("Name1", "Description1");
        taskManager.updateTask(task1);
        taskManager.getEpicById(1);
        Task oldTask = taskManager.getHistory().getFirst();
        assertEquals(task.getTitle(), oldTask.getTitle());
        assertEquals(task.getDescription(), oldTask.getDescription());
        assertEquals(task.getId(), oldTask.getId());
        assertEquals(task.getStatus(), oldTask.getStatus());
    }
}