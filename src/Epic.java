import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public boolean isDone() {
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != StatusTask.DONE) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Epic{" +
                super.toString() +
                "subtasks=" + subtasks +
                '}';
    }
}
