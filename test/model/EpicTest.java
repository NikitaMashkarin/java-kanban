package model;

import com.yandex.taskTracker.model.Epic;
import com.yandex.taskTracker.model.StatusTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {
    @Test
    public void epicsWithEqualIdShouldBeEqual() {
        Epic epic1 = new Epic("Name2", "Description2", 1, StatusTask.NEW);
        Epic epic2 = new Epic("Name1", "Description1", 1, StatusTask.NEW);
        Assertions.assertEquals(epic1, epic2, "Епки должны быть равны, если равны их id");
    }
}