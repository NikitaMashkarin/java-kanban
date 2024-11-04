package com.yandex.taskTracker.service;

import com.yandex.taskTracker.exception.ValidationException;
import com.yandex.taskTracker.model.*;
import com.yandex.taskTracker.model.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int nextID = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        if (t1.getStartTime() == null) {
            if (t2.getStartTime() == null) {
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

    public Set<Task> getPrioritizedTasks() {
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
        if (checkValidationTasks(task)) {
            task.setId(getNextID());
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (checkValidationTasks(epic)) {
            epic.setId(getNextID());
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (checkValidationTasks(subtask)) {
            subtask.setId(getNextID());
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            subtasks.put(subtask.getId(), subtask);
            calculationStatusEpic(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (checkValidationTasks(task)) {
            prioritizedTasks.remove(tasks.get(id));
            prioritizedTasks.add(task);
            tasks.put(id, task);
        }
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
        if (checkValidationTasks(subtask)) {
            prioritizedTasks.remove(oldSubtask);
            prioritizedTasks.add(subtask);
            subtasks.put(subtaskId, subtask);
            Epic epic = epics.get(epicId);
            List<Subtask> oldSubtasks = epic.getSubtasks();
            oldSubtasks.remove(oldSubtask);
            oldSubtasks.add(subtask);
            calculationStatusEpic(epic);
        }
    }

    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(tasks.remove(id));
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
        Subtask subtask = subtasks.remove(id);
        prioritizedTasks.remove(subtask);
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

    private boolean checkValidationTasks(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        boolean isValid = getPrioritizedTasks().stream()
                .filter(validationTask -> startTime != null && endTime != null && !task.equals(validationTask))
                .anyMatch(validationTask -> {
                    LocalDateTime entryStartTime = validationTask.getStartTime();
                    LocalDateTime entryEndTime = validationTask.getEndTime();
                    return !(endTime.isBefore(entryStartTime));
                });

        if (isValid) {
            throw new ValidationException("Задача " + task.getId() + " пересекается с другой задачей.");
        }
        prioritizedTasks.add(task);
        return true;
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
