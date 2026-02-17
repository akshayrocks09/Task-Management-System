package com.assignment.TaskManagementSystem.repository;

import com.assignment.TaskManagementSystem.entity.Task;
import com.assignment.TaskManagementSystem.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByUserId(UUID userId);

    List<Task> findByUserIdAndStatus(UUID userId, TaskStatus status);

    Optional<Task> findByIdAndUserId(UUID taskId, UUID userId);
}
