package com.yandex.taskTracker.test.service;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.StatusTask;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void anEpikCannotBeAddedToItselfAsASubtask() {
        Epic epic = new Epic("Name", "Description");
        Subtask subtask = new Subtask("Name", "Description", 1, StatusTask.NEW, 1);
        assertDoesNotThrow(() -> {
            epic.addSubtask(subtask);
        });
    }

    @Test
    public void inMemoryTaskManagerAddsTasksOfDifferentTypesAndFindsThemById() {
        Task task = new Task("Name", "Description", 1, StatusTask.NEW);
        taskManager.addTask(task);
        assertNotNull(taskManager.getTaskById(1));
        Epic epic = new Epic("Name", "Description", 1, StatusTask.NEW);
        taskManager.addEpic(epic);
        assertNotNull(taskManager.getEpicById(2));
        Subtask subtask = new Subtask("Name", "Description", 1, StatusTask.NEW, 2);
        taskManager.addSubtask(subtask);
        assertNotNull(taskManager.getSubtaskById(3));
    }

    @Test
    public void managerReturnsInitialized() {
        assertNotNull(taskManager);
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    public void tasksWithTheSpecifiedIdAndTheGeneratedIdDoNotConflict() {
        Task task1 = new Task("Name", "Description", 1, StatusTask.NEW);
        taskManager.addTask(task1);
        Task task = new Task("Name", "Description");
        taskManager.addTask(task);
        assertEquals(taskManager.getAllTasks().size(), 2);

    }

    @Test
    public void immutabilityOfTheTaskWhenAddedToTheManager() {
        Task task = new Task("Name", "Description", 1, StatusTask.NEW);
        taskManager.addTask(task);
        Task task1 = taskManager.getTaskById(1);
        assertEquals(task.getTitle(), task1.getTitle());
        assertEquals(task.getDescription(), task1.getDescription());
        assertEquals(task.getId(), task1.getId());
        assertEquals(task.getStatus(), task1.getStatus());
    }
}