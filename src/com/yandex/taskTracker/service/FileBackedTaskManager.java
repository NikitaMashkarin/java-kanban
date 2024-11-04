package com.yandex.taskTracker.service;

import com.yandex.taskTracker.exception.ManagerSaveException;
import com.yandex.taskTracker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,epic,startTime,duration\n";

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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
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

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            InMemoryTaskManager taskManager = Managers.getDefault();

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String line;
                int id = 1;
                while ((line = br.readLine()) != null) {
                    Task task = fromString(line);

                    int idTask = task.getId();
                    if (idTask > id) {
                        id = idTask;
                    }

                    if (task != null) {
                        if (task.getType().equals(TypeTask.EPIC)) {
                            taskManager.getEpics().put(task.getId(), (Epic) task);
                        } else if (task.getType().equals(TypeTask.SUBTASK)) {
                            taskManager.getSubtasks().put(task.getId(), (Subtask) task);
                        } else {
                            taskManager.getTasks().put(task.getId(), task);
                        }
                    }
                }
                taskManager.setNextID(id);
            } catch (IOException e) {
                throw new ManagerSaveException("Произошла ошибка при чтении файла: " + e.getMessage(), e);
            }

            return fileBackedTaskManager;
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Некорректный файл: " + e.getMessage(), e);
        }
    }

    private static Task fromString(String value) {
        String[] tasksArray = value.split(",");
        Task task;
        String statusStr = tasksArray[3];

        StatusTask status = StatusTask.valueOf(statusStr);

        String typeTaskStr = tasksArray[1];

        TypeTask typeTask = TypeTask.valueOf(typeTaskStr);
        String name = tasksArray[2];
        String description = tasksArray[4];
        LocalDateTime startTime = null;
        Duration duration = null;
        if (tasksArray[5] != null) {
            startTime = LocalDateTime.parse(tasksArray[5]);
        }
        if (tasksArray[6] != null) {
            duration = Duration.ofMinutes(Long.parseLong(tasksArray[6]));
        }
        int id = Integer.parseInt(tasksArray[0]);
        int epicId = Integer.parseInt(tasksArray[5]);
        switch (typeTask) {
            case TypeTask.TASK -> {
                task = new Task(name, description, id, status, duration, startTime);
                return task;
            }
            case TypeTask.EPIC -> {
                task = new Epic(name, description, id, status, duration, startTime);
                return task;
            }
            case TypeTask.SUBTASK -> {
                task = new Subtask(name, description, id, status, epicId, duration, startTime);
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
                + task.getDescription() + "," + task.getEpicId() + "," + task.getStartTime().toString()
                + "," + task.getDuration().toString();
    }
}
