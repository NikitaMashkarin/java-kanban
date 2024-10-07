package com.yandex.taskTracker;

import com.yandex.taskTracker.service.*;
import com.yandex.taskTracker.model.*;

public class Main {
    public static void main (String[] args){
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task washFloor = new Task("Помыть полы", "С новым средством");
        taskManager.addTask(washFloor);
        System.out.println(washFloor);

        Task washFloorToUpdate = new Task("Не забыть помыть полы", "Можно и без средства",
                washFloor.getId(), StatusTask.IN_PROGRESS);
        Task washFloorUpdated = taskManager.updateTask(washFloorToUpdate);
        System.out.println(washFloorUpdated);
    }
}
