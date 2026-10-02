package com.employee.management.service;

import com.employee.management.dto.LeaveBalanceDto;
import com.employee.management.dto.LeaveRequestDto;
import com.employee.management.dto.LeaveResponseDto;

import java.util.List;

public interface LeaveService {
    LeaveResponseDto applyLeave(String email, LeaveRequestDto requestDto);
    List<LeaveResponseDto> getMyLeaves(String email);
    List<LeaveResponseDto> getAllLeaves();
    LeaveResponseDto approveLeave(Long id, String actionedByEmail);
    LeaveResponseDto rejectLeave(Long id, String actionedByEmail);
    LeaveResponseDto cancelLeave(Long id, String email);
    List<LeaveBalanceDto> getMyLeaveBalance(String email);
}