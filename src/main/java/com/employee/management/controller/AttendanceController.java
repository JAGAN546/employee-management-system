package com.employee.management.controller;

import com.employee.management.dto.AttendanceResponseDto;
import com.employee.management.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponseDto> checkIn(Authentication authentication) {
        AttendanceResponseDto response = attendanceService.checkIn(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceResponseDto> checkOut(Authentication authentication) {
        return ResponseEntity.ok(attendanceService.checkOut(authentication.getName()));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-history")
    public ResponseEntity<List<AttendanceResponseDto>> getMyHistory(
            Authentication authentication) {
        return ResponseEntity.ok(attendanceService.getMyHistory(authentication.getName()));
    }
}