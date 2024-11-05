package com.yandex.taskTracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description, int id, StatusTask status, Duration duration,
                LocalDateTime startTime) {
        super(title, description, id, status, duration, startTime);
    }

    public Epic(String title, String description, int id, StatusTask status) {
        super(title, description, id, status);
    }

    public Epic(String title, String description) {
        super(title, description);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        endTime = maxEndTime();
    }

    public void clearSubtask() {
        subtasks.clear();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "com.yandex.taskTracker.model.Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime =" + getStartTime() +
                ", endTime = " + getDuration() +
                '}';
    }

    private LocalDateTime maxEndTime() {
        LocalDateTime maxEndTime = subtasks.getFirst().getEndTime();
        for (Subtask sub : subtasks) {
            LocalDateTime endTimeSubtask = sub.getStartTime();
            if (maxEndTime.isBefore(endTimeSubtask)) {
                maxEndTime = endTimeSubtask;
            }
        }
        return maxEndTime;
    }
}