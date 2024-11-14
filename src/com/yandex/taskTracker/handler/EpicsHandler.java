package com.yandex.taskTracker.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.service.BaseHttpHandler;
import com.yandex.taskTracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
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
            sendText(httpExchange, gson.toJson(taskManager.getAllEpics()));
        }
        if (pathParts.length == 3) {
            Optional<Integer> id = getEpicId(pathParts[2]);
            if (id.isPresent()) {
                Epic epic = taskManager.getEpicById(id.get());
                if (epic != null) {
                    sendText(httpExchange, gson.toJson(epic));
                } else {
                    sendNotFound(httpExchange, "Задача с id " + id.get() + " не найдена");
                }
            }
        }

        if (pathParts.length == 4) {
            Optional<Integer> id = getEpicId(pathParts[2]);
            if (id.isPresent()) {
                Epic epic = taskManager.getEpicById(id.get());
                if (epic != null) {
                    sendText(httpExchange, gson.toJson(epic.getSubtasks()));
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
        Epic epic = gson.fromJson(jsonObject, Epic.class);
        if (pathParts.length == 2) {
            taskManager.addEpic(epic);
            if (taskManager.getEpicById(epic.getId()).equals(epic)) {
                sendHasInteractions(httpExchange, 201, "Епик добавлен");
            }
        }

        if (pathParts.length == 3) {
            Optional<Integer> id = getEpicId(pathParts[2]);
            if (id.isPresent()) {
                taskManager.updateEpic(epic);
                if (taskManager.getEpicById(epic.getId()).equals(epic)) {
                    sendHasInteractions(httpExchange, 201, "Епик изменен");
                }
            }
        }
    }

    private void delete(HttpExchange httpExchange) throws IOException {
        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3) {
            Optional<Integer> id = getEpicId(pathParts[2]);
            if (id.isPresent()) {
                taskManager.removeEpicById(id.get());
                if (taskManager.getEpicById(id.get()) == null) {
                    sendHasInteractions(httpExchange, 201, "Епик удален");
                }
            }
        }
    }

    protected static Optional<Integer> getEpicId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}