package model;

import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.model.StatusTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task("Name", "Description", 1, StatusTask.NEW);
        Task task2 = new Task("Name1", "Description1", 1, StatusTask.NEW);
        Assertions.assertEquals(task1, task2, "Задачи должны быть равны, если равны их id");
    }
}