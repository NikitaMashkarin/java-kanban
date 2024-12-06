package com.yandex.taskTracker.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.service.BaseHttpHandler;
import com.yandex.taskTracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                get(httpExchange);
            case "POST":
                post(httpExchange);
            case "DELETE":
                delete(httpExchange);
            default:
                try (OutputStream os = httpExchange.getResponseBody()) {
                    httpExchange.sendResponseHeaders(405, 0);
                    os.write("Метод не найден".getBytes(StandardCharsets.UTF_8));
                }
        }
        httpExchange.close();
    }

    private void get(HttpExchange httpExchange) throws IOException {

        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            sendText(httpExchange, gson.toJson(taskManager.getAllTasks()));
        }
        if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                Task task = taskManager.getTaskById(id.get());
                if (task != null) {
                    sendText(httpExchange, gson.toJson(task));
                } else {
                    sendNotFound(httpExchange, "Задача с id " + id.get() + " не найдена");
                }
            }
        }
    }

    private void post(HttpExchange httpExchange) throws IOException {
        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            sendHasInteractions(httpExchange, 406, "Не удовлетворяет");
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task = gson.fromJson(jsonObject, Task.class);
        if (pathParts.length == 2) {
            taskManager.addTask(task);
            if (taskManager.getTaskById(task.getId()).equals(task)) {
                sendHasInteractions(httpExchange, 201, "Задача добавлена");
            } else {
                sendHasInteractions(httpExchange, 406, "Задача с id" + task.getId()
                        + "пересекается с другой задачей");
            }
        }

        if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                taskManager.updateTask(task);
                if (taskManager.getTaskById(task.getId()).equals(task)) {
                    sendHasInteractions(httpExchange, 201, "Задача изменена");
                } else {
                    sendHasInteractions(httpExchange, 406, "Задача с id" + task.getId()
                            + "пересекается с другой задачей");
                }
            }
        }
    }

    private void delete(HttpExchange httpExchange) throws IOException {
        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                taskManager.removeTaskById(id.get());
                if (taskManager.getTaskById(id.get()) == null) {
                    sendHasInteractions(httpExchange, 201, "Задача удалена");
                }
            }
        }
    }

    protected static Optional<Integer> getTaskId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
