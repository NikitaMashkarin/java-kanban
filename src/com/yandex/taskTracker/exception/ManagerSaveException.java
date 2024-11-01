package com.yandex.taskTracker.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
