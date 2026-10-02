package com.employee.management.service.impl;

import com.employee.management.dto.EmployeeSummaryDto;
import com.employee.management.dto.TaskRequestDto;
import com.employee.management.dto.TaskResponseDto;
import com.employee.management.dto.TaskStatusUpdateDto;
import com.employee.management.entity.*;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.exception.EmployeeProfileNotLinkedException;
import com.employee.management.exception.InvalidTaskStatusException;
import com.employee.management.exception.TaskNotFoundException;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.TaskRepository;
import com.employee.management.repository.UserRepository;
import com.employee.management.service.TaskService;
import com.employee.management.entity.NotificationType;
import com.employee.management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final Set<TaskStatus> EMPLOYEE_ALLOWED_STATUSES =
            Set.of(TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED);

    @Override
    public TaskResponseDto createTask(TaskRequestDto requestDto, String assignedByEmail) {
        Employee assignedTo = employeeRepository.findById(requestDto.getAssignedToId())
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with id: " + requestDto.getAssignedToId()));

        Task task = new Task();
        task.setTitle(requestDto.getTitle());
        task.setDescription(requestDto.getDescription());
        task.setAssignedTo(assignedTo);
        task.setAssignedBy(assignedByEmail);
        task.setPriority(requestDto.getPriority());
        task.setDueDate(requestDto.getDueDate());

        Task saved = taskRepository.save(task);

        if (assignedTo.getUser() != null) {
            notificationService.createNotification(
                    assignedTo.getUser(),
                    "You have been assigned a new task: " + task.getTitle(),
                    NotificationType.TASK_ASSIGNED);
        }

        return toDto(saved);
    }

    @Override
    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<TaskResponseDto> getMyTasks(String email) {
        Employee employee = resolveEmployee(email);
        return taskRepository.findByAssignedToIdOrderByDueDateAsc(employee.getId())
                .stream().map(this::toDto).toList();
    }

    @Override
    public TaskResponseDto updateMyTaskStatus(Long id, String email, TaskStatusUpdateDto statusDto) {
        Employee employee = resolveEmployee(email);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        if (!task.getAssignedTo().getId().equals(employee.getId())) {
            throw new AccessDeniedException("You can only update tasks assigned to you");
        }

        if (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.CANCELLED) {
            throw new InvalidTaskStatusException(
                    "Cannot change status of a " + task.getStatus() + " task");
        }

        if (!EMPLOYEE_ALLOWED_STATUSES.contains(statusDto.getStatus())) {
            throw new InvalidTaskStatusException(
                    "You can only set status to IN_PROGRESS or COMPLETED");
        }

        task.setStatus(statusDto.getStatus());
        return toDto(taskRepository.save(task));
    }

    private Employee resolveEmployee(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));

        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));
    }

    private TaskResponseDto toDto(Task task) {
        Employee employee = task.getAssignedTo();

        EmployeeSummaryDto summary = EmployeeSummaryDto.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .build();

        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .assignedTo(summary)
                .assignedBy(task.getAssignedBy())
                .priority(task.getPriority())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}