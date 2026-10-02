package com.employee.management.controller;

import com.employee.management.dto.TaskRequestDto;
import com.employee.management.dto.TaskResponseDto;
import com.employee.management.dto.TaskStatusUpdateDto;
import com.employee.management.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @Valid @RequestBody TaskRequestDto requestDto,
            Authentication authentication) {
        TaskResponseDto response = taskService.createTask(requestDto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<TaskResponseDto>> getMyTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getMyTasks(authentication.getName()));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateMyTaskStatus(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody TaskStatusUpdateDto statusDto) {
        return ResponseEntity.ok(
                taskService.updateMyTaskStatus(id, authentication.getName(), statusDto));
    }
}