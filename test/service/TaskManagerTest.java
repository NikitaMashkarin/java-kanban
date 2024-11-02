package service;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.StatusTask;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldReturnNewWhenAllSubtasksAreNew() {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, StatusTask.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, StatusTask.NEW,
                epic.getId(), Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(StatusTask.NEW, epic.getStatus(), "Статус должен быть НОВЫМ, " +
                "когда все подзадачи являются НОВЫМИ");
    }

    @Test
    void shouldReturnDoneWhenAllSubtasksAreDone() {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, StatusTask.DONE, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, StatusTask.DONE, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(StatusTask.DONE, epic.getStatus(), "Статус должен быть ВЫПОЛЕНЫМ, " +
                "когда все подзадачи являются ВЫПОЛЕННЫМИ");
    }

    @Test
    void shouldReturnInProgressWhenSubtasksAreNewAndDone() {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, StatusTask.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, StatusTask.DONE, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS, " +
                "когда подзадачи являются НОВЫМИ и ВЫПОЛНЕНЫ");
    }

    @Test
    void shouldReturnInProgressWhenAllSubtasksAreInProgress() {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, StatusTask.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, StatusTask.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS, " +
                "когда все подзадачи находятся в процессе выполнения");
    }


}
