package com.yandex.taskTracker.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.service.BaseHttpHandler;
import com.yandex.taskTracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
            sendText(httpExchange, gson.toJson(taskManager.getAllSubtask()));
        }
        if (pathParts.length == 3) {
            Optional<Integer> id = getSubtaskId(pathParts[2]);
            if (id.isPresent()) {
                Subtask subtask = taskManager.getSubtaskById(id.get());
                if (subtask != null) {
                    sendText(httpExchange, gson.toJson(subtask));
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
        Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
        if (pathParts.length == 2) {
            taskManager.addSubtask(subtask);
            if (taskManager.getSubtaskById(subtask.getId()).equals(subtask)) {
                sendHasInteractions(httpExchange, 201, "Задача добавлена");
            } else {
                sendHasInteractions(httpExchange, 406, "Задача с id" + subtask.getId()
                        + "пересекается с другой задачей");
            }
        }

        if (pathParts.length == 3) {
            Optional<Integer> id = getSubtaskId(pathParts[2]);
            if (id.isPresent()) {
                taskManager.updateSubtask(subtask);
                if (taskManager.getSubtaskById(subtask.getId()).equals(subtask)) {
                    sendHasInteractions(httpExchange, 201, "Задача изменена");
                } else {
                    sendHasInteractions(httpExchange, 406, "Задача с id" + subtask.getId()
                            + "пересекается с другой задачей");
                }
            }
        }
    }

    private void delete(HttpExchange httpExchange) throws IOException {
        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3) {
            Optional<Integer> id = getSubtaskId(pathParts[2]);
            if (id.isPresent()) {
                taskManager.removeSubtaskById(id.get());
                if (taskManager.getSubtaskById(id.get()) == null) {
                    sendHasInteractions(httpExchange, 201, "Задача удалена");
                }
            }
        }
    }

    protected static Optional<Integer> getSubtaskId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}


