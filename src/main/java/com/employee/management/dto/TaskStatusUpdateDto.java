package com.employee.management.dto;

import com.employee.management.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusUpdateDto {

    @NotNull(message = "Status is required")
    private TaskStatus status;
}