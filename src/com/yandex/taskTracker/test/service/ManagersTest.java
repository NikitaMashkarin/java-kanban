package com.yandex.taskTracker.test.service;

import com.yandex.taskTracker.service.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void utilityClassAlwaysReturnsInitializedInstancesInMemoryTaskManager() {
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    public void utilityClassAlwaysReturnsInitializedInstancesInMemoryHistoryManager() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }
}