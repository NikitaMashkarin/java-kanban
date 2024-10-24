package com.yandex.taskTracker.File;
import com.yandex.taskTracker.model.*;
import com.yandex.taskTracker.service.HistoryManager;
import com.yandex.taskTracker.service.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,epic\n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task){
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic){
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
    public void deleteEpics(){
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks(){
        super.deleteSubtasks();
        save();
    }

    @Override
    public Task updateTask(Task task){
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public void updateEpic(Epic epic){
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask){
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id){
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id){
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id){
        super.removeSubtaskById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try(BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))){
            String line;
            while ((line = br.readLine()) != null) {
                Task task = fromString(line);
                if(task != null) {
                    if (task instanceof Epic epic) {
                        fileBackedTaskManager.addEpic(epic);
                    } else if (task instanceof Subtask subtask) {
                        fileBackedTaskManager.addSubtask(subtask);
                    } else {
                        fileBackedTaskManager.addTask(task);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка при чтении файла: " + e.getMessage());
        }

        return fileBackedTaskManager;
    }

    private static Task fromString(String value) {
        String[] tasksArray = value.split(",");
        Task task;
        StatusTask status;

        switch (tasksArray[3]){
            case "NEW":
                status = StatusTask.NEW;
                break;
            case "IN_PROGRESS":
                status = StatusTask.IN_PROGRESS;
                break;
            default:
                status = StatusTask.DONE;
                break;
        }

        if (tasksArray[1].equals("TASK")){
            task = new Task(tasksArray[2], tasksArray[4], Integer.parseInt(tasksArray[0]), status);
            return task;
        } else if (tasksArray[1].equals("EPIC")) {
            task = new Epic(tasksArray[2], tasksArray[4], Integer.parseInt(tasksArray[0]), status);
            return task;
        } else if (tasksArray[1].equals("SUBTASK")) {
            task = new Subtask(tasksArray[2], tasksArray[4], Integer.parseInt(tasksArray[0]), status,
                    Integer.parseInt(tasksArray[5]));
            return task;
        }
        return null;
    }

    private void save(){
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных", e);
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_CSV_FILE);

            for(Task task : super.getAllTasks()){
                writer.write(toString(task, TypeTask.TASK) + "\n");
            }

            for (Epic epic : super.getAllEpics()){
                writer.write(toString(epic, TypeTask.EPIC)+ "\n");
            }

            for (Subtask subtask : super.getAllSubtask()){
                writer.write(toString(subtask, TypeTask.SUBTASK) + "\n");
            }

            writer.write("\n");
        } catch(IOException e){
            throw new ManagerSaveException("Не удалось сохранить файл", e);
        }

    }

    private String toString(Task task, TypeTask type){
        return task.getId() + "," + type.toString() + "," + task.getTitle() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId();
    }
}
