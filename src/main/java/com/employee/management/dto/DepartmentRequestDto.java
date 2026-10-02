package com.employee.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequestDto {

    @NotBlank(message = "Department name is required")
    private String name;

    private String description;
}