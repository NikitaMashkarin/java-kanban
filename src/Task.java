public class Task {
    private final String title;
    private final String description;
    private int id = 0;
    private StatusTask status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        id++;
        this.status = StatusTask.NEW;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
