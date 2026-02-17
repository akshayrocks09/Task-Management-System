package com.assignment.TaskManagementSystem.service;

import com.assignment.TaskManagementSystem.dto.TaskRequest;
import com.assignment.TaskManagementSystem.dto.TaskResponse;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest);
    List<TaskResponse> getAllTasks();
    TaskResponse getTaskById(UUID id);
    TaskResponse updateTask(UUID id, TaskRequest taskRequest);
    void deleteTask(UUID id);
    List<TaskResponse> getTasksByUserId(UUID userId);
}