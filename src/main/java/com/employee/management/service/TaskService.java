package com.employee.management.service;

import com.employee.management.dto.TaskRequestDto;
import com.employee.management.dto.TaskResponseDto;
import com.employee.management.dto.TaskStatusUpdateDto;

import java.util.List;

public interface TaskService {
    TaskResponseDto createTask(TaskRequestDto requestDto, String assignedByEmail);
    List<TaskResponseDto> getAllTasks();
    List<TaskResponseDto> getMyTasks(String email);
    TaskResponseDto updateMyTaskStatus(Long id, String email, TaskStatusUpdateDto statusDto);
}