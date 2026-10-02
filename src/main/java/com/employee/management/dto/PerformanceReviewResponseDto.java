package com.employee.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReviewResponseDto {
    private Long id;
    private EmployeeSummaryDto employee;
    private String reviewedBy;
    private String reviewPeriod;
    private Integer rating;
    private String strengths;
    private String improvements;
    private String comments;
    private LocalDateTime createdAt;
}