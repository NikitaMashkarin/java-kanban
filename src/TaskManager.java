import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int nextID = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getAllTasks () {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics () {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
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
        calculationStatusEpic(epic);
    }

    public Task updateTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
        return task;
    }

    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        oldEpic.setDescription(epic.getDescription());
        oldEpic.setTitle(epic.getTitle());
    }

    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask oldSubtask = subtasks.get(subtaskId);
        subtasks.remove(subtaskId);
        subtasks.put(subtaskId, subtask);
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> oldSubtasks = epic.getSubtasks();
        oldSubtasks.remove(oldSubtask);
        oldSubtasks.add(subtask);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        ArrayList<Subtask> epicSubtask = epics.get(id).getSubtasks();
        epics.remove(id);
        for (Subtask subtask : epicSubtask) {
            subtasks.remove(subtask.getId());
        }
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtaskArrayList = epic.getSubtasks();
        subtaskArrayList.remove(subtask);
        calculationStatusEpic(epic);
    }

    public ArrayList<Subtask> getSubtaskCertainEpic(int id) {
        return new ArrayList<>(epics.get(id).getSubtasks());
    }

    private void calculationStatusEpic(Epic epic) {
        int statusNew = 0;
        int statusDone = 0;

        ArrayList<Subtask> subtask = epic.getSubtasks();
        for (Subtask subt : subtask) {
            if (subt.getStatus() == StatusTask.NEW) {
                statusNew++;
            } else {
                if (subt.getStatus() != StatusTask.IN_PROGRESS) {
                    statusDone++;
                }
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