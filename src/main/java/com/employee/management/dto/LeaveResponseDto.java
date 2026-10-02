package com.employee.management.dto;

import com.employee.management.entity.LeaveStatus;
import com.employee.management.entity.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponseDto {
    private Long id;
    private EmployeeSummaryDto employee;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private LocalDateTime appliedAt;
    private String actionedBy;
    private LocalDateTime actionedAt;
}