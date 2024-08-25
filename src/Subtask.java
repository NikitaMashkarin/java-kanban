public class Subtask extends Task {
    private int epicId = 0;

    public Subtask(String title, String description) {
        super(title, description);
        epicId++;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                super.toString() +
                "epicId=" + epicId +
                '}';
    }
}
