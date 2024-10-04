package service;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.service.Managers;
import com.yandex.taskTracker.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void s(){
        Epic flatRenovation = new Epic("Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(flatRenovation);
        Subtask flatRenovationSubtask1 = new Subtask("Поклеить обои", "Обязательно светлые!",
                flatRenovation.getId());
        Subtask flatRenovationSubtask2 = new Subtask("Установить новую технику","Старую продать на Авито",
                flatRenovation.getId());

    }
}