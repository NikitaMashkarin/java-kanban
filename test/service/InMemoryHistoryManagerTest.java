package service;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.service.InMemoryHistoryManager;
import com.yandex.taskTracker.service.Managers;
import com.yandex.taskTracker.service.TaskManager;
import com.yandex.taskTracker.service.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void tasksAddedToTheHistoryManagerRetainItsPreviousVersion() {
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

    @Test
    public void theHistoryCannotSaveDuplicates() {
        Task task = new Task("Name", "Description");
        taskManager.addTask(task);
        for (int i = 0; i <= 15; i++) {
            taskManager.getTaskById(1);
        }
        assertEquals(taskManager.getHistory().size(), 1);
    }

    @Test
    public void removeMethodRemovesTaskFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task flatRenovation = new Task("Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addTask(flatRenovation);
        historyManager.add(flatRenovation);
        historyManager.remove(flatRenovation.getId());
        assertEquals( taskManager.getHistory().size(), 0);
    }

    @Test
    public void addMethodAddsTaskFromHistory() {
        Epic flatRenovation = new Epic("Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(flatRenovation);
        taskManager.getEpicById(1);
        assertEquals(taskManager.getHistory().size(), 1);
    }
}