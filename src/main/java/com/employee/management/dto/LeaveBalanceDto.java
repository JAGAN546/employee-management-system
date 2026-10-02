package com.employee.management.dto;

import com.employee.management.entity.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceDto {
    private LeaveType leaveType;
    private int totalDays;
    private int usedDays;
    private int remainingDays;
}