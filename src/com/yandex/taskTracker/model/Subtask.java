package com.yandex.taskTracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, StatusTask status, int epicId, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "com.yandex.taskTracker.model.Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", startTime =" + getStartTime() +
                ", endTime = " + getDuration() +
                '}';
    }
}
