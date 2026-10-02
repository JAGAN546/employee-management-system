package com.employee.management.service;

import com.employee.management.dto.AttendanceResponseDto;

import java.util.List;

public interface AttendanceService {
    AttendanceResponseDto checkIn(String email);
    AttendanceResponseDto checkOut(String email);
    List<AttendanceResponseDto> getMyHistory(String email);
}