package com.employee.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDto {
    private long totalEmployees;
    private long activeEmployees;
    private long totalDepartments;
    private long employeesOnLeaveToday;
    private long todaysAttendanceCount;
    private long pendingLeaveRequests;
    private List<EmployeeSummaryDto> recentEmployees;
}