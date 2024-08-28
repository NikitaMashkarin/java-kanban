import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int nextID = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> printAllTasks () {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task != null) {
                allTasks.add(task);
            }
        }
        return allTasks;
    }

    public ArrayList<Epic> printAllEpics () {
        ArrayList <Epic> allEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            if (epic != null) {
                allEpics.add(epic);
            }
        }
        return allEpics;
    }

    public ArrayList<Subtask> printAllSubtask() {
        ArrayList<Subtask> allSubtask = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask != null) {
                allSubtask.add(subtask);
            }
        }
        return allSubtask;
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            epic.setStatus(StatusTask.NEW);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void addTask(Task task){
        task.setId(getNextID());
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic){
        epic.setId(getNextID());
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask){
        subtask.setId(getNextID());
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        calculationStatusEpic(epics.get(subtask.getEpicId()));
    }

    public Task updateTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
        return task;
    }

    public void updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        ArrayList<Subtask> oldSubtask = epic.getSubtasks();
        if (!oldSubtask.isEmpty()) {
            for (Subtask subtask : oldSubtask) {
                subtasks.remove(subtask.getId());
            }
        }

        epics.remove(epicId);
        epics.put(epicId, epic);

        ArrayList<Subtask> newSubtask = epic.getSubtasks();
        if (!newSubtask.isEmpty()) {
            for (Subtask subtask : newSubtask) {
                subtasks.put(subtask.getId(), subtask);
            }
        }
    }

    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask oldSubtask = subtasks.get(subtaskId);
        subtasks.remove(subtaskId);
        subtasks.put(subtaskId, subtask);
        Epic epic = getEpicById(epicId);
        ArrayList<Subtask> oldSubtasks = epic.getSubtasks();
        oldSubtasks.remove(subtask);
        oldSubtasks.add(oldSubtask);
        epic.setSubtasks(oldSubtasks);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        epics.get(id).clearSubtask();
        ArrayList<Subtask> epicSubtask = epics.get(id).getSubtasks();
        epics.remove(id);
        for (Subtask subtask : epicSubtask) {
            subtasks.remove(subtask.getId());
        }
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        int EpicId = subtask.getEpicId();
        subtasks.remove(id);
        Epic epic = getEpicById(EpicId);
        ArrayList<Subtask> subtaskArrayList = epic.getSubtasks();
        subtaskArrayList.remove(subtask);
        epic.setSubtasks(subtaskArrayList);

    }

    public ArrayList<Subtask> getSubtaskCertainEpic(int id) {
        Epic epic = getEpicById(id);
        ArrayList<Subtask> subtasksCertainEpic = new ArrayList<>();
        subtasksCertainEpic = epic.getSubtasks();
        return subtasksCertainEpic;
    }

    private void calculationStatusEpic(Epic epic) {
        int statusNew = 0;
        int statusDone = 0;

        ArrayList<Subtask> subtask = epic.getSubtasks();
        for (Subtask subt : subtask) {
            if (subt.getStatus() == StatusTask.NEW) {
                statusNew++;
            } else {
                statusDone++;
            }
        }
        int subtaskSize = subtask.size();
        if (statusNew == subtaskSize) {
            epic.setStatus(StatusTask.NEW);
        } else if (statusDone == subtaskSize) {
            epic.setStatus(StatusTask.DONE);
        } else  {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }
    }

    private int getNextID() {
        return nextID++;
    }
}