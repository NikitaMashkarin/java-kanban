package com.yandex.taskTracker.service;

import com.yandex.taskTracker.model.*;
import com.yandex.taskTracker.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int nextID = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>((t1,t2) -> {
        if(t1.getStartTime() == null){
            if(t2.getStartTime() == null){
                return 0;
            }
            return 1;
        }
        return t1.getStartTime().compareTo(t2.getStartTime());
    });

    protected Map<Integer, Task> getTasks() {
        return tasks;
    }

    protected void setNextID(int nextID) {
        this.nextID = nextID;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            epic.setStatus(StatusTask.NEW);
        }
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        task.setId(getNextID());
        tasks.put(task.getId(), task);
        addPrioritizedTasks(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNextID());
        epics.put(epic.getId(), epic);
        addPrioritizedTasks(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(getNextID());
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        calculationStatusEpic(epic);
        addPrioritizedTasks(subtask);
    }

    @Override
    public Task updateTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
        addPrioritizedTasks(task);
        return task;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        oldEpic.setDescription(epic.getDescription());
        addPrioritizedTasks(epic);
        oldEpic.setTitle(epic.getTitle());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask oldSubtask = subtasks.get(subtaskId);
        subtasks.put(subtaskId, subtask);
        Epic epic = epics.get(epicId);
        List<Subtask> oldSubtasks = epic.getSubtasks();
        oldSubtasks.remove(oldSubtask);
        oldSubtasks.add(subtask);
        calculationStatusEpic(epic);
        addPrioritizedTasks(subtask);
    }

    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        prioritizedTasks.remove(epics.get(id));
        List<Subtask> epicSubtask = epics.remove(id).getSubtasks();
        historyManager.remove(id);
        for (Subtask subtask : epicSubtask) {
            prioritizedTasks.remove(subtask);
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        prioritizedTasks.remove(subtasks.get(id));
        Subtask subtask = subtasks.remove(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        List<Subtask> subtaskList = epic.getSubtasks();
        subtaskList.remove(subtask);
        calculationStatusEpic(epic);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtaskCertainEpic(int id) {
        return new ArrayList<>(epics.get(id).getSubtasks());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void addPrioritizedTasks(Task task){
        if(task.getStartTime() == null){
            return;
        }

        if(prioritizedTasks.contains(task)){
            prioritizedTasks.remove(task);
        }
        prioritizedTasks.add(task);
    }

    private void calculationStatusEpic(Epic epic) {
        int statusNew = 0;
        int statusDone = 0;

        List<Subtask> subtasks = epic.getSubtasks();
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
