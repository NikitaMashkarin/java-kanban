package com.yandex.taskTracker.File;

import com.yandex.taskTracker.model.*;
import com.yandex.taskTracker.service.InMemoryTaskManager;
import com.yandex.taskTracker.service.Managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,epic\n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            InMemoryTaskManager taskManager = Managers.getDefault();
            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Task task = fromString(line);
                    if (task != null) {
                        if (task.getType().equals(TypeTask.EPIC)) {
                            Epic epic = new Epic(task.getTitle(), task.getDescription(), task.getId(), task.getStatus());
                            taskManager.getEpics().put(task.getId(), epic);
                        } else if (task.getType().equals(TypeTask.SUBTASK)) {
                            Subtask subtask = new Subtask(task.getTitle(), task.getDescription(), task.getId(),
                                    task.getStatus(), task.getEpicId());
                            taskManager.getSubtasks().put(task.getId(), subtask);
                        } else {
                            taskManager.getTasks().put(task.getId(), task);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Произошла ошибка при чтении файла: " + e.getMessage());
            }

            return fileBackedTaskManager;
        } catch (ManagerSaveException e) {
            System.out.println("Некорректный файл");
        }
        return null;
    }

    private static Task fromString(String value) {
        String[] tasksArray = value.split(",");
        Task task;
        String statusStr = tasksArray[3];

        if (statusStr.equals("status")) {
            return null;
        }

        StatusTask status = StatusTask.valueOf(statusStr);

        String typeTaskStr = tasksArray[1];

        if (statusStr.equals("type")) {
            return null;
        }

        TypeTask typeTask = TypeTask.valueOf(typeTaskStr);
        String name = tasksArray[2];
        String description = tasksArray[4];
        int id = Integer.parseInt(tasksArray[0]);
        int epicId = Integer.parseInt(tasksArray[5]);
        switch (typeTask) {
            case TypeTask.TASK -> {
                task = new Task(name, description, id, status);
                return task;
            }
            case TypeTask.EPIC -> {
                task = new Epic(name, description, id, status);
                return task;
            }
            case TypeTask.SUBTASK -> {
                task = new Subtask(name, description, id, status, epicId);
                return task;
            }
        }
        return null;
    }

    private void save() {
        try {
            Path path = file.toPath();
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.createFile(path);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных", e);
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_CSV_FILE);

            for (Task task : super.getAllTasks()) {
                writer.write(toString(task, TypeTask.TASK) + "\n");
            }

            for (Epic epic : super.getAllEpics()) {
                writer.write(toString(epic, TypeTask.EPIC) + "\n");
            }

            for (Subtask subtask : super.getAllSubtask()) {
                writer.write(toString(subtask, TypeTask.SUBTASK) + "\n");
            }

            writer.write("\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить файл", e);
        }

    }

    private String toString(Task task, TypeTask type) {
        return task.getId() + "," + type.toString() + "," + task.getTitle() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId();
    }
}
