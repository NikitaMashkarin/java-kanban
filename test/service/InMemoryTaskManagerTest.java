package service;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.StatusTask;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void epikCannotBeAddedToItselfAsASubtask() {
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

    @Test
    public void shouldNotKeepOldIdAfterSubtaskIsDeleted() {
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.removeSubtaskById(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertFalse(taskManager.getAllSubtask().contains(subtask));
    }

    @Test
    public void shouldNotKeepNonActualSubtaskIdsInEpic() {
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Subtask description", epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.removeSubtaskById(subtask1.getId());
        List<Subtask> subtasksInEpic = taskManager.getSubtaskCertainEpic(epic.getId());
        assertEquals(1, subtasksInEpic.size());
        assertTrue(subtasksInEpic.contains(subtask2));
        assertFalse(subtasksInEpic.contains(subtask1));
    }

    @Test
    public void shouldUpdateTaskFieldsAndReflectInManager() {
        Task task = new Task("Task1", "Task description");
        taskManager.addTask(task);
        task.setTitle("Updated Task");
        task.setDescription("Updated description");
        task.setStatus(StatusTask.DONE);
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        assertEquals(StatusTask.DONE, updatedTask.getStatus());
    }

    @Test
    public void shouldUpdateEpicAndPreserveSubtasks() {
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        taskManager.addSubtask(subtask);
        epic.setTitle("Updated Epic");
        epic.setDescription("Updated description");
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals("Updated Epic", updatedEpic.getTitle());
        assertEquals("Updated description", updatedEpic.getDescription());
        List<Subtask> subtasksInEpic = taskManager.getSubtaskCertainEpic(epic.getId());
        assertEquals(1, subtasksInEpic.size());
        assertTrue(subtasksInEpic.contains(subtask));
    }

    @Test
    public void byDeletingTaskItIsDeletedFromHistory(){
        Task task = new Task("Task1", "Task description");
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.removeTaskById(task.getId());
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingEpicItIsDeletedFromHistory() {
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());
        taskManager.removeEpicById(epic.getId());
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingEpicsWithSubtasksItIsDeletedFromHistory(){
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.removeEpicById(epic.getId());
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingSubtaskItIsDeletedFromHistory(){
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.getSubtaskById(subtask.getId());
        taskManager.removeSubtaskById(subtask.getId());
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingAllTasksTheyAreDeletedFromTheHistory(){
        Task task = new Task("Task1", "Task description");
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        Task task1 = new Task("Task1", "Task description");
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.deleteTasks();
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingAllEpicsTheyAreDeletedFromTheHistory(){
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());
        Epic epic1 = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        taskManager.deleteEpics();
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingAllEpicsWithSubtasksTheyAreDeletedFromTheHistory(){
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        Epic epic1 = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.deleteEpics();
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test
    public void byDeletingAllSubtasksTheyAreDeletedFromTheHistory(){
        Epic epic = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Subtask description", epic.getId());
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.getSubtaskById(subtask.getId());
        taskManager.addSubtask(subtask1);
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.deleteSubtasks();
        assertEquals(taskManager.getHistory().size(), 0);
    }
}