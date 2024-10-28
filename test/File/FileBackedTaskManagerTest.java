package File;

import com.yandex.taskTracker.service.FileBackedTaskManager;
import com.yandex.taskTracker.model.*;
import com.yandex.taskTracker.service.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends InMemoryTaskManager {
    private FileBackedTaskManager fileManager;
    private File file;

    @Test
    public void tasksIsSavedToAFile() throws IOException {
        file = File.createTempFile("test", ".csv");
        fileManager = new FileBackedTaskManager(file);
        Task task = new Task("Name", "Description", 1, StatusTask.NEW);
        fileManager.addTask(task);
        String taskStr = task.getId() + "," + "TASK" + "," + task.getTitle() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId();
        Task task1 = new Task("Name", "Description", 2, StatusTask.NEW);
        fileManager.addTask(task1);
        String taskStr1 = task1.getId() + "," + "TASK" + "," + task1.getTitle() + "," + task1.getStatus() + ","
                + task1.getDescription() + "," + task1.getEpicId();
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
    public void epicsIsSavedToAFile() throws IOException {
        file = File.createTempFile("test", ".csv");
        fileManager = new FileBackedTaskManager(file);
        Epic epic = new Epic("Name", "Description", 1, StatusTask.NEW);
        fileManager.addEpic(epic);
        String taskStr = epic.getId() + "," + "EPIC" + "," + epic.getTitle() + "," + epic.getStatus() + ","
                + epic.getDescription() + "," + epic.getEpicId();
        Epic epic1 = new Epic("Name", "Description", 2, StatusTask.NEW);
        fileManager.addEpic(epic1);
        String taskStr1 = epic1.getId() + "," + "EPIC" + "," + epic1.getTitle() + "," + epic1.getStatus() + ","
                + epic1.getDescription() + "," + epic1.getEpicId();
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
    public void subtasksIsSavedToAFile() throws IOException {
        file = File.createTempFile("test", ".csv");
        fileManager = new FileBackedTaskManager(file);
        Epic epic = new Epic("Name", "Description", 1, StatusTask.NEW);
        fileManager.addEpic(epic);
        Subtask subtask = new Subtask("Name", "Description", 2, StatusTask.NEW, 1);
        fileManager.addSubtask(subtask);
        String taskStr = subtask.getId() + "," + "SUBTASK" + "," + subtask.getTitle() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getEpicId();
        Subtask subtask1 = new Subtask("Name", "Description", 3, StatusTask.NEW, 1);
        fileManager.addSubtask(subtask1);
        String taskStr1 = subtask1.getId() + "," + "SUBTASK" + "," + subtask1.getTitle() + "," + subtask1.getStatus()
                + "," + subtask1.getDescription() + "," + subtask1.getEpicId();
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
    public void savingAndUploadingAnEmptyFile() throws IOException {
        file = File.createTempFile("test", ".csv");
        fileManager = new FileBackedTaskManager(file);
        assertEquals((fileManager.getAllTasks().size() + fileManager.getAllEpics().size()
                + fileManager.getAllSubtask().size()), 0);
    }
}