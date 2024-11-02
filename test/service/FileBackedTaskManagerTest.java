package service;

import com.yandex.taskTracker.exception.ManagerSaveException;
import com.yandex.taskTracker.service.FileBackedTaskManager;
import com.yandex.taskTracker.model.*;
import com.yandex.taskTracker.service.InMemoryTaskManager;
import com.yandex.taskTracker.service.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    public void init() throws IOException {
        file = File.createTempFile("test", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    public void tasksIsSavedToAFile() {
        manager = new FileBackedTaskManager(file);
        Task task = new Task("Name", "Description", 1, StatusTask.NEW);
        manager.addTask(task);
        String taskStr = task.getId() + "," + "TASK" + "," + task.getTitle() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId() + "," + task.getStartTime() + "," + task.getDuration();
        Task task1 = new Task("Name", "Description", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(40)));
        manager.addTask(task1);
        String taskStr1 = task1.getId() + "," + "TASK" + "," + task1.getTitle() + "," + task1.getStatus() + ","
                + task1.getDescription() + "," + task1.getEpicId() + "," + task1.getStartTime() + "," + task1.getDuration();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            for (int i = 0; i < 3; i++) {
                line = br.readLine();
                if (i == 1) {
                    assertEquals(taskStr, line);
                }
                if (i == 2) {
                    assertEquals(taskStr1, line);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void epicsIsSavedToAFile() {
        manager = new FileBackedTaskManager(file);
        Epic epic = new Epic("Name", "Description", 1, StatusTask.NEW);
        manager.addEpic(epic);
        String taskStr = epic.getId() + "," + "EPIC" + "," + epic.getTitle() + "," + epic.getStatus() + ","
                + epic.getDescription() + "," + epic.getEpicId() + "," + epic.getStartTime() + "," + epic.getDuration();
        Epic epic1 = new Epic("Name", "Description", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(20)));
        manager.addEpic(epic1);
        String taskStr1 = epic1.getId() + "," + "EPIC" + "," + epic1.getTitle() + "," + epic1.getStatus() + ","
                + epic1.getDescription() + "," + epic1.getEpicId() + "," + epic1.getStartTime() + "," + epic.getDuration();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            for (int i = 0; i < 3; i++) {
                line = br.readLine();
                if (i == 1) {
                    assertEquals(taskStr, line);
                }
                if (i == 2) {
                    assertEquals(taskStr1, line);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void subtasksIsSavedToAFile() {
        manager = new FileBackedTaskManager(file);
        Epic epic = new Epic("Name", "Description", 1, StatusTask.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Name", "Description", 2, StatusTask.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addSubtask(subtask);
        String taskStr = subtask.getId() + "," + "SUBTASK" + "," + subtask.getTitle() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getEpicId() + "," + subtask.getStartTime() + "," + subtask.getDuration();
        Subtask subtask1 = new Subtask("Name", "Description", 3, StatusTask.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.now().plus(Duration.ofMinutes(20)));
        manager.addSubtask(subtask1);
        String taskStr1 = subtask1.getId() + "," + "SUBTASK" + "," + subtask1.getTitle() + "," + subtask1.getStatus()
                + "," + subtask1.getDescription() + "," + subtask1.getEpicId() + "," + subtask1.getStartTime() + "," + subtask1.getDuration();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            for (int i = 0; i < 4; i++) {
                line = br.readLine();
                if (i == 2) {
                    assertEquals(taskStr, line);
                }
                if (i == 3) {
                    assertEquals(taskStr1, line);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void savingAndUploadingAnEmptyFile() {
        manager = new FileBackedTaskManager(file);
        assertEquals((manager.getAllTasks().size() + manager.getAllEpics().size()
                + manager.getAllSubtask().size()), 0);
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenFileCannotBeRead() {
        File invalidFile = new File("/invalid_path/test_tasks.csv");

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(invalidFile);
        }, "Ожидается выброс ManagerSaveException");
    }
}