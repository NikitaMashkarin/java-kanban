import java.util.HashMap;
import  java.util.Scanner;

public class TaskManager {
    public static void main (String[] args) {
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        HashMap<Integer, Subtask> subtasks = new HashMap<>();


    }

    public static void printAllTasks (HashMap<Integer, Task> tasks, HashMap<Integer, Epic> epics,
                                      HashMap<Integer, Subtask> subtasks) {
        System.out.println("Все задачи:");

        for (Task task : tasks.values()) {
            if (task != null) {
                System.out.println(task.toString());
            }
        }

        for (Task epic : epics.values()) {
            if (epic != null) {
                System.out.println(epic.toString());
            }
        }

        for (Task subtask : subtasks.values()) {
            if (subtask != null) {
                System.out.println(subtask.toString());
            }
        }
    }

    public static void removeAllTasks (HashMap<Integer, Task> tasks, HashMap<Integer, Epic> epics,
                    HashMap<Integer, Subtask> subtasks) {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }


}
