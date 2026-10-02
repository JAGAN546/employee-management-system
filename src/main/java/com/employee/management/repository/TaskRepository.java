package com.employee.management.repository;

import com.employee.management.entity.Task;
import com.employee.management.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedToIdOrderByDueDateAsc(Long employeeId);

    List<Task> findAllByOrderByCreatedAtDesc();

    long countByAssignedToId(Long employeeId);

    long countByAssignedToIdAndStatus(Long employeeId, TaskStatus status);
}