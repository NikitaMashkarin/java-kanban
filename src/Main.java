public class Main {
    public static void main (String[] args){
        TaskManager taskManager = new TaskManager();
        Task washTheFloor = new Task("Помыть пол", "...");
        taskManager.addTask(washTheFloor);
        taskManager.printAllTasks();
        System.out.println(taskManager.getTaskById(1));

        Epic washTheWindow = new Epic("Помыть окна", "...");
        taskManager.addEpic(washTheWindow);
        System.out.println(taskManager.getEpicById(2));
        taskManager.printAllEpics();

        Subtask subtaskWashTheWindow = new Subtask("Набрать воду", "...", washTheWindow.getId());
        taskManager.addSubtask(subtaskWashTheWindow);
        subtaskWashTheWindow = new Subtask("Намочить тряпку", "...", washTheWindow.getId());
        taskManager.addSubtask(subtaskWashTheWindow);
        taskManager.printAllSubtask();
        System.out.println(taskManager.getSudtaskById(3));
        taskManager.getSubtaskCertainEpic(2);
        subtaskWashTheWindow.setStatus(StatusTask.DONE);
        taskManager.calculationStatusEpic(washTheWindow);
        taskManager.printAllEpics();

        Task waterTheFlowers = new Task("Полить цветы", "...");
        taskManager.updateTask(waterTheFlowers);
        Task waterTheRose = new Task("Полить розы", "...");
        taskManager.updateTask(waterTheRose);
        taskManager.printAllTasks();
        taskManager.removeTaskById(1);
        taskManager.printAllTasks();
        taskManager.removeSubtaskById(3);
        taskManager.printAllEpics();
        taskManager.removeEpicById(2);
        taskManager.printAllEpics();

        taskManager.printAllSubtask();

        Epic buyBread  = new Epic("купить хлеб", "...");
        taskManager.updateEpic(buyBread);
        taskManager.printAllEpics();

        Subtask buyMilk = new Subtask("купить иолоко", "...");
        taskManager.updateSubtask(buyMilk);
        taskManager.printAllSubtask();

        taskManager.deleteTasks();
        taskManager.printAllTasks();
        taskManager.deleteSubtasks();
        taskManager.printAllSubtask();
        taskManager.deleteEpics();
        taskManager.printAllEpics();

    }
}
