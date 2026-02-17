package com.assignment.TaskManagementSystem.service.impl;

import com.assignment.TaskManagementSystem.dto.TaskRequest;
import com.assignment.TaskManagementSystem.dto.TaskResponse;
import com.assignment.TaskManagementSystem.entity.Role;
import com.assignment.TaskManagementSystem.entity.Task;
import com.assignment.TaskManagementSystem.entity.TaskStatus;
import com.assignment.TaskManagementSystem.entity.User;
import com.assignment.TaskManagementSystem.exception.ResourceNotFoundException;
import com.assignment.TaskManagementSystem.exception.UnauthorizedException;
import com.assignment.TaskManagementSystem.repository.TaskRepository;
import com.assignment.TaskManagementSystem.repository.UserRepository;
import com.assignment.TaskManagementSystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
        User currentUser = getCurrentUser();

        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .status(taskRequest.getStatus() != null ? taskRequest.getStatus() : TaskStatus.PENDING)
                .user(currentUser)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        User currentUser = getCurrentUser();

        List<Task> tasks;
        if (currentUser.getRole() == Role.ADMIN) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByUserId(currentUser.getId());
        }

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTaskById(UUID id) {
        User currentUser = getCurrentUser();

        Task task;
        if (currentUser.getRole() == Role.ADMIN) {
            task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        } else {
            task = taskRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));
        }

        return mapToTaskResponse(task);
    }

    @Override
    public TaskResponse updateTask(UUID id, TaskRequest taskRequest) {
        Task existingTask = getTaskEntity(id);

        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        if (taskRequest.getStatus() != null) {
            existingTask.setStatus(taskRequest.getStatus());
        }

        Task updatedTask = taskRepository.save(existingTask);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    public void deleteTask(UUID id) {
        Task task = getTaskEntity(id);
        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponse> getTasksByUserId(UUID userId) {
        // Verify the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Task> tasks = taskRepository.findByUserId(userId);
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    // Helper method to get task entity with security checks
    private Task getTaskEntity(UUID id) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        } else {
            return taskRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElseThrow(() -> new UnauthorizedException("Access denied to task with id: " + id));
        }
    }

    // Helper to get the logged-in user from the Security Context
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // Helper method to map Task entity to TaskResponse DTO
    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .userId(task.getUser().getId())
                .userName(task.getUser().getName())
                .userEmail(task.getUser().getEmail())
                .build();
    }
}