import java.util.HashMap;
import  java.util.Scanner;

public class TaskManager {
    public static void main (String[] args) {
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        HashMap<Integer, Subtask> subtasks = new HashMap<>();

    }

    public static void printAllTasks (HashMap<Integer, Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        for (Task task : tasks.values()) {
            if (task != null) {
                System.out.println(task.toString());
            }
        }
    }


    public void getAllEpics (HashMap<Integer, Epic> epics) {
        if (epics.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        for (Task epic : epics.values()) {
            if (epic != null) {
                System.out.println(epic.toString());
            }
        }
    }

    public void getAllSubtasks (HashMap<Integer, Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        for (Task subtask : subtasks.values()) {
            if (subtask != null) {
                System.out.println(subtask.toString());
            }
        }
    }
}
