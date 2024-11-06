package service;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.StatusTask;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.service.HistoryManager;
import com.yandex.taskTracker.service.Managers;
import com.yandex.taskTracker.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void epikCannotBeAddedToItselfAsASubtask() {
        Epic epic = new Epic("Name", "Description");
        Subtask subtask = new Subtask("Name", "Description", 1, StatusTask.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.now());
        assertDoesNotThrow(() -> {
            epic.addSubtask(subtask);
        });
    }

    @Test
    public void inMemoryTaskManagerAddsTasksOfDifferentTypesAndFindsThemById() {
        Task task = new Task("Name", "Description", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(task);
        assertNotNull(manager.getTaskById(1));
        Epic epic = new Epic("Name", "Description", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(20)));
        manager.addEpic(epic);
        assertNotNull(manager.getEpicById(2));
        Subtask subtask = new Subtask("Name", "Description", 1, StatusTask.NEW, 2,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addSubtask(subtask);
        assertNotNull(manager.getSubtaskById(3));
    }

    @Test
    public void managerReturnsInitialized() {
        assertNotNull(manager);
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    public void tasksWithTheSpecifiedIdAndTheGeneratedIdDoNotConflict() {
        Task task = new Task("Name", "Description");
        manager.addTask(task);
        Task task1 = new Task("Name", "Description", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addTask(task1);
        assertEquals(manager.getAllTasks().size(), 2);

    }

    @Test
    public void immutabilityOfTheTaskWhenAddedToTheManager() {
        Task task = new Task("Name", "Description", 1, StatusTask.NEW);
        manager.addTask(task);
        Task task1 = manager.getTaskById(1);
        assertEquals(task.getTitle(), task1.getTitle());
        assertEquals(task.getDescription(), task1.getDescription());
        assertEquals(task.getId(), task1.getId());
        assertEquals(task.getStatus(), task1.getStatus());
    }

    @Test
    public void shouldNotKeepOldIdAfterSubtaskIsDeleted() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", 2, StatusTask.DONE,
                epic.getId(), Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addSubtask(subtask);
        manager.removeSubtaskById(subtask.getId());
        assertNull(manager.getSubtaskById(subtask.getId()));
        assertFalse(manager.getAllSubtask().contains(subtask));
    }

    @Test
    public void shouldNotKeepNonActualSubtaskIdsInEpic() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", 2, StatusTask.NEW,
                epic.getId(), Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(20)));
        Subtask subtask2 = new Subtask("Subtask2", "Subtask description", 3, StatusTask.NEW,
                epic.getId(), Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.removeSubtaskById(subtask1.getId());
        List<Subtask> subtasksInEpic = manager.getSubtaskCertainEpic(epic.getId());
        assertEquals(1, subtasksInEpic.size());
        assertTrue(subtasksInEpic.contains(subtask2));
        assertFalse(subtasksInEpic.contains(subtask1));
    }

    @Test
    public void shouldUpdateTaskFieldsAndReflectInManager() {
        Task task = new Task("Task1", "Task description");
        manager.addTask(task);
        task.setTitle("Updated Task");
        task.setDescription("Updated description");
        task.setStatus(StatusTask.DONE);
        Task updatedTask = manager.getTaskById(task.getId());
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        assertEquals(StatusTask.DONE, updatedTask.getStatus());
    }

    @Test
    public void shouldUpdateEpicAndPreserveSubtasks() {
        Epic epic = new Epic("Epic1", "Epic description", 1, StatusTask.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", 2, StatusTask.NEW,
                epic.getId(), Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addSubtask(subtask);
        epic.setTitle("Updated Epic");
        epic.setDescription("Updated description");
        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals("Updated Epic", updatedEpic.getTitle());
        assertEquals("Updated description", updatedEpic.getDescription());
        List<Subtask> subtasksInEpic = manager.getSubtaskCertainEpic(epic.getId());
        assertEquals(1, subtasksInEpic.size());
        assertTrue(subtasksInEpic.contains(subtask));
    }

    @Test
    public void byDeletingTaskItIsDeletedFromHistory() {
        Task task = new Task("Task1", "Task description");
        manager.addTask(task);
        manager.getTaskById(task.getId());
        manager.removeTaskById(task.getId());
        assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingEpicItIsDeletedFromHistory() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);
        manager.getEpicById(epic.getId());
        manager.removeEpicById(epic.getId());
        assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingEpicsWithSubtasksItIsDeletedFromHistory() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        manager.addSubtask(subtask);
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        manager.removeEpicById(epic.getId());
        assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingSubtaskItIsDeletedFromHistory() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        manager.addSubtask(subtask);
        manager.getSubtaskById(subtask.getId());
        manager.removeSubtaskById(subtask.getId());
        assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingAllTasksTheyAreDeletedFromTheHistory() {
        Task task = new Task("Task1", "Task description");
        manager.addTask(task);
        manager.getTaskById(task.getId());
        Task task1 = new Task("Task1", "Task description", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addTask(task1);
        manager.getTaskById(task1.getId());
        manager.deleteTasks();
        assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingAllEpicsTheyAreDeletedFromTheHistory() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);
        manager.getEpicById(epic.getId());
        Epic epic1 = new Epic("Epic1", "Epic description", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addEpic(epic1);
        manager.getEpicById(epic1.getId());
        manager.deleteEpics();
        assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingAllEpicsWithSubtasksTheyAreDeletedFromTheHistory() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", 2, StatusTask.NEW,  epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(20)));
        manager.addSubtask(subtask);
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        Epic epic1 = new Epic("Epic1", "Epic description", 3, StatusTask.NEW);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", 4, StatusTask.NEW,
                epic1.getId(), Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addSubtask(subtask1);
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.deleteEpics();
        assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingAllSubtasksTheyAreDeletedFromTheHistory() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", 3, StatusTask.NEW,
                epic.getId(), Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addSubtask(subtask);
        manager.getSubtaskById(subtask.getId());
        manager.addSubtask(subtask1);
        manager.getSubtaskById(subtask1.getId());
        manager.deleteSubtasks();
        assertEquals(manager.getHistory().size(), 0);
    }
}
