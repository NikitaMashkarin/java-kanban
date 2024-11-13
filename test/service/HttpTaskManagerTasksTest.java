package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.Subtask;
import com.yandex.taskTracker.model.Task;
import com.yandex.taskTracker.model.StatusTask;
import com.yandex.taskTracker.service.HttpTaskServer;
import com.yandex.taskTracker.service.InMemoryTaskManager;
import com.yandex.taskTracker.service.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static HttpTaskServer taskServer;
    private static TaskManager manager;
    private static HttpClient client;
    private static Gson gson;

    @BeforeAll
    static void setUpClass() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
        gson = taskServer.getGson();
        taskServer.start();
    }

    @AfterAll
    static void tearDownClass() {
        taskServer.stop();
    }

    @BeforeEach
    void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();

    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", 1, StatusTask.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertNotNull(manager.getAllTasks());
        assertEquals(1, manager.getAllTasks().size());
        assertEquals("Task", manager.getAllTasks().get(0).getTitle());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", 1, StatusTask.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", 2, StatusTask.DONE, Duration.ofMinutes(20), LocalDateTime.now().plusHours(1));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", 1, StatusTask.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        manager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", 1, StatusTask.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        manager.addTask(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getId(), returnedTask.getId());
        assertEquals("Task", returnedTask.getTitle());
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Test", "Epic Description", 1, StatusTask.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        manager.addEpic(epic);
        manager.addSubtask(new Subtask("Subtask Test", "Subtask Description", epic.getId()));
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size());
        assertEquals("Epic Test", epics.get(0).getTitle());
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(new Subtask("Subtask Test", "Subtask Description", epic1.getId()));
        manager.addSubtask(new Subtask("Subtask 2", "Description 2", 2, StatusTask.NEW, epic2.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plus(Duration.ofMinutes(20))));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, epics.length);
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for Subtask", "Epic Description");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask Test", "Subtask Description", epic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = manager.getAllSubtask();
        assertEquals(1, subtasks.size());
        assertEquals("Subtask Test", subtasks.get(0).getTitle());
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 2, StatusTask.NEW, epic.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plus(Duration.ofMinutes(20)));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(2, subtasks.length);
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(new Subtask("Subtask Test", "Subtask Description", epic1.getId()));
        manager.addSubtask(new Subtask("Subtask 2", "Description 2", 2, StatusTask.NEW, epic2.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plus(Duration.ofMinutes(20))));
        manager.getEpicById(1);
        manager.getEpicById(2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, history.length);
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task2 = new Task("Task 1", "Description 1", 1, StatusTask.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task1 = new Task("Task 2", "Description 2", 2, StatusTask.DONE, Duration.ofMinutes(20), LocalDateTime.now().plusHours(1));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
        assertEquals(tasks[1], task1);
        assertEquals(tasks[0], task2);
    }
}