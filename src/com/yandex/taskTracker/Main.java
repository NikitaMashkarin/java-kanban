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


        Epic flatRenovation = new Epic("Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(flatRenovation);
        System.out.println(flatRenovation);
        Subtask flatRenovationSubtask1 = new Subtask("Поклеить обои", "Обязательно светлые!",
                flatRenovation.getId());
        Subtask flatRenovationSubtask2 = new Subtask("Установить новую технику","Старую продать на Авито",
                flatRenovation.getId());
        taskManager.addSubtask(flatRenovationSubtask1);
        taskManager.addSubtask(flatRenovationSubtask2);
        System.out.println(flatRenovation);
        flatRenovationSubtask2.setStatus(StatusTask.DONE);
        taskManager.updateSubtask(flatRenovationSubtask2);
        System.out.println(flatRenovation);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);

        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);

        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getHistory().size());
    }
}
