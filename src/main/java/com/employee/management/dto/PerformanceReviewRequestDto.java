package com.employee.management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PerformanceReviewRequestDto {

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotBlank(message = "Review period is required")
    private String reviewPeriod;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String strengths;
    private String improvements;
    private String comments;
}