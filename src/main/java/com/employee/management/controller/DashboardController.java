package com.employee.management.controller;

import com.employee.management.dto.AdminDashboardDto;
import com.employee.management.dto.EmployeeDashboardDto;
import com.employee.management.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardDto> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee")
    public ResponseEntity<EmployeeDashboardDto> getEmployeeDashboard(
            Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getEmployeeDashboard(authentication.getName()));
    }
}