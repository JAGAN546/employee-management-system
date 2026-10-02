package com.employee.management.controller;

import com.employee.management.dto.LeaveBalanceDto;
import com.employee.management.dto.LeaveRequestDto;
import com.employee.management.dto.LeaveResponseDto;
import com.employee.management.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<LeaveResponseDto> applyLeave(
            Authentication authentication,
            @Valid @RequestBody LeaveRequestDto requestDto) {
        LeaveResponseDto response = leaveService.applyLeave(authentication.getName(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<LeaveResponseDto>> getMyLeaves(Authentication authentication) {
        return ResponseEntity.ok(leaveService.getMyLeaves(authentication.getName()));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-balance")
    public ResponseEntity<List<LeaveBalanceDto>> getMyLeaveBalance(Authentication authentication) {
        return ResponseEntity.ok(leaveService.getMyLeaveBalance(authentication.getName()));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<LeaveResponseDto> cancelLeave(
            @PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(leaveService.cancelLeave(id, authentication.getName()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping
    public ResponseEntity<List<LeaveResponseDto>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<LeaveResponseDto> approveLeave(
            @PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(leaveService.approveLeave(id, authentication.getName()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<LeaveResponseDto> rejectLeave(
            @PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(leaveService.rejectLeave(id, authentication.getName()));
    }
}