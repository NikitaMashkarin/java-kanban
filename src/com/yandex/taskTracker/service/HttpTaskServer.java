package com.yandex.taskTracker.service;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    static class TasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    GET(httpExchange);
                case "POST":
                    POST(httpExchange);
                case "DELETE":
                    DELETE(httpExchange);
                default:
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        httpExchange.sendResponseHeaders(405, 0);
                        os.write("Метод не найден".getBytes(StandardCharsets.UTF_8));
                    }
            }
            httpExchange.close();
        }

        private void GET(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();

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

        private void POST(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();
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
                if (taskManager.getTasks().get(task.getId()).equals(task)) {
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
                    if (taskManager.getTasks().get(task.getId()).equals(task)) {
                        sendHasInteractions(httpExchange, 201, "Задача изменена");
                    } else {
                        sendHasInteractions(httpExchange, 406, "Задача с id" + task.getId()
                                + "пересекается с другой задачей");
                    }
                }
            }
        }

        private void DELETE(HttpExchange httpExchange) throws IOException {
            String requestPath = httpExchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");

            if (pathParts.length == 3) {
                Optional<Integer> id = getTaskId(pathParts[2]);
                if (id.isPresent()) {
                    taskManager.removeTaskById(id.get());
                    if (taskManager.getTasks().get(id.get()) == null) {
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

    static class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    GET(httpExchange);
                case "POST":
                    POST(httpExchange);
                case "DELETE":
                    DELETE(httpExchange);
                default:
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        httpExchange.sendResponseHeaders(405, 0);
                        os.write("Метод не найден".getBytes(StandardCharsets.UTF_8));
                    }
            }
            httpExchange.close();
        }

        private void GET(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();

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

        private void POST(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();
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
                if (taskManager.getSubtasks().get(subtask.getId()).equals(subtask)) {
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
                    if (taskManager.getSubtasks().get(subtask.getId()).equals(subtask)) {
                        sendHasInteractions(httpExchange, 201, "Задача изменена");
                    } else {
                        sendHasInteractions(httpExchange, 406, "Задача с id" + subtask.getId()
                                + "пересекается с другой задачей");
                    }
                }
            }
        }

        private void DELETE(HttpExchange httpExchange) throws IOException {
            String requestPath = httpExchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");

            if (pathParts.length == 3) {
                Optional<Integer> id = getSubtaskId(pathParts[2]);
                if (id.isPresent()) {
                    taskManager.removeSubtaskById(id.get());
                    if (taskManager.getSubtasks().get(id.get()) == null) {
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

    static class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    GET(httpExchange);
                case "POST":
                    POST(httpExchange);
                case "DELETE":
                    DELETE(httpExchange);
                default:
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        httpExchange.sendResponseHeaders(405, 0);
                        os.write("Метод не найден".getBytes(StandardCharsets.UTF_8));
                    }
            }
            httpExchange.close();
        }

        private void GET(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();

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

        private void POST(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();
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
                if (taskManager.getEpics().get(epic.getId()).equals(epic)) {
                    sendHasInteractions(httpExchange, 201, "Епик добавлен");
                }
            }

            if (pathParts.length == 3) {
                Optional<Integer> id = getEpicId(pathParts[2]);
                if (id.isPresent()) {
                    taskManager.updateEpic(epic);
                    if (taskManager.getEpics().get(epic.getId()).equals(epic)) {
                        sendHasInteractions(httpExchange, 201, "Епик изменен");
                    }
                }
            }
        }

        private void DELETE(HttpExchange httpExchange) throws IOException {
            String requestPath = httpExchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");

            if (pathParts.length == 3) {
                Optional<Integer> id = getEpicId(pathParts[2]);
                if (id.isPresent()) {
                    taskManager.removeEpicById(id.get());
                    if (taskManager.getEpics().get(id.get()) == null) {
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

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if (httpExchange.getRequestMethod().equals("GET")) {
                GET(httpExchange);
            } else {
                try (OutputStream os = httpExchange.getResponseBody()) {
                    httpExchange.sendResponseHeaders(405, 0);
                    os.write("Метод не найден".getBytes(StandardCharsets.UTF_8));
                }
            }
            httpExchange.close();
        }

        private void GET(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();

            String requestPath = httpExchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");

            if (pathParts.length == 2) {
                sendText(httpExchange, gson.toJson(taskManager.getHistory()));
            }
        }
    }

    static class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if (httpExchange.getRequestMethod().equals("GET")) {
                GET(httpExchange);
            } else {
                try (OutputStream os = httpExchange.getResponseBody()) {
                    httpExchange.sendResponseHeaders(405, 0);
                    os.write("Метод не найден".getBytes(StandardCharsets.UTF_8));
                }
            }
            httpExchange.close();
        }

        private void GET(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();

            String requestPath = httpExchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");

            if (pathParts.length == 2) {
                sendText(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()));
            }
        }
    }

    static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss.SSS");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(dateTimeFormatter));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), dateTimeFormatter);
        }
    }

    static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value(String.valueOf(duration));
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            return Duration.parse(jsonReader.nextString());
        }
    }
}
