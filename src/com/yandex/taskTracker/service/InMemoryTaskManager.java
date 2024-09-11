package com.yandex.taskTracker.service;

import com.yandex.taskTracker.model.*;
import com.yandex.taskTracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements  TaskManager{
    private int nextID = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getAllTasks () {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics () {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            epic.setStatus(StatusTask.NEW);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void addTask(Task task){
        task.setId(getNextID());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic){
        epic.setId(getNextID());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask){
        subtask.setId(getNextID());
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        calculationStatusEpic(epic);
    }

    @Override
    public Task updateTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
        return task;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        oldEpic.setDescription(epic.getDescription());
        oldEpic.setTitle(epic.getTitle());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask oldSubtask = subtasks.get(subtaskId);
        subtasks.put(subtaskId, subtask);
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> oldSubtasks = epic.getSubtasks();
        oldSubtasks.remove(oldSubtask);
        oldSubtasks.add(subtask);
        calculationStatusEpic(epic);
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        ArrayList<Subtask> epicSubtask = epics.remove(id).getSubtasks();
        for (Subtask subtask : epicSubtask) {
            subtasks.remove(subtask.getId());
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtaskArrayList = epic.getSubtasks();
        subtaskArrayList.remove(subtask);
        calculationStatusEpic(epic);
    }

    @Override
    public ArrayList<Subtask> getSubtaskCertainEpic(int id) {
        return new ArrayList<>(epics.get(id).getSubtasks());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void calculationStatusEpic(Epic epic) {
        int statusNew = 0;
        int statusDone = 0;

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            final StatusTask status = subtask.getStatus();
            if (status == StatusTask.NEW) {
                statusNew++;
            } else if (status == StatusTask.DONE) {
                statusDone++;
            } else {
                epic.setStatus(StatusTask.IN_PROGRESS);
                return;
            }
        }
        int subtaskSize = subtasks.size();
        if (statusNew == subtaskSize) {
            epic.setStatus(StatusTask.NEW);
        } else if (statusDone == subtaskSize) {
            epic.setStatus(StatusTask.DONE);
        } else {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }
    }

    private int getNextID() {
        return nextID++;
    }
}
