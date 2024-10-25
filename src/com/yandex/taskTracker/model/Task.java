package com.yandex.taskTracker.model;

import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private StatusTask status;

    public Task(String title, String description, int id, StatusTask status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        ;
        this.status = StatusTask.NEW;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public StatusTask getStatus() {
        return status;
    }

    public Integer getEpicId() {
        return null;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "com.yandex.taskTracker.model.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }
}
