package com.employee.management.service;

import com.employee.management.dto.AdminDashboardDto;
import com.employee.management.dto.EmployeeDashboardDto;

public interface DashboardService {
    AdminDashboardDto getAdminDashboard();
    EmployeeDashboardDto getEmployeeDashboard(String email);
}