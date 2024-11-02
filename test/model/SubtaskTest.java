package model;

import com.yandex.taskTracker.model.StatusTask;
import com.yandex.taskTracker.model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {
    @Test
    public void subtasksWithEqualIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Name2", "Description2", 1, StatusTask.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Name1", "Description1", 1, StatusTask.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(20)));
        Assertions.assertEquals(subtask1, subtask2, "Подзадачи должны быть равны, если равны их id");
    }
}