package com.employee.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDashboardDto {
    private LocalTime todayCheckInTime;
    private LocalTime todayCheckOutTime;
    private boolean checkedInToday;
    private List<LeaveBalanceDto> leaveBalances;
    private long pendingLeaveCount;
    private long totalAssignedTasks;
    private long completedTasks;
    private long unreadNotificationCount;
}