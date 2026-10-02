package com.employee.management.service.impl;

import com.employee.management.dto.*;
import com.employee.management.entity.*;
import com.employee.management.exception.EmployeeProfileNotLinkedException;
import com.employee.management.repository.*;
import com.employee.management.service.DashboardService;
import com.employee.management.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final LeaveService leaveService;

    @Override
    public AdminDashboardDto getAdminDashboard() {
        LocalDate today = LocalDate.now();

        List<EmployeeSummaryDto> recent = employeeRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(e -> EmployeeSummaryDto.builder()
                        .id(e.getId())
                        .employeeCode(e.getEmployeeCode())
                        .firstName(e.getFirstName())
                        .lastName(e.getLastName())
                        .build())
                .toList();

        return AdminDashboardDto.builder()
                .totalEmployees(employeeRepository.count())
                .activeEmployees(employeeRepository.countByStatus(EmployeeStatus.ACTIVE))
                .totalDepartments(departmentRepository.count())
                .employeesOnLeaveToday(leaveRepository.countEmployeesOnLeaveToday(today))
                .todaysAttendanceCount(attendanceRepository.countByDate(today))
                .pendingLeaveRequests(leaveRepository.countByStatus(LeaveStatus.PENDING))
                .recentEmployees(recent)
                .build();
    }

    @Override
    public EmployeeDashboardDto getEmployeeDashboard(String email) {
        Employee employee = resolveEmployee(email);
        LocalDate today = LocalDate.now();

        Optional<Attendance> todayAttendance =
                attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today);

        List<LeaveBalanceDto> balances = leaveService.getMyLeaveBalance(email);

        long pendingLeave = leaveRepository
                .findByEmployeeIdAndStatus(employee.getId(), LeaveStatus.PENDING)
                .size();

        return EmployeeDashboardDto.builder()
                .todayCheckInTime(todayAttendance.map(Attendance::getCheckInTime).orElse(null))
                .todayCheckOutTime(todayAttendance.map(Attendance::getCheckOutTime).orElse(null))
                .checkedInToday(todayAttendance.isPresent())
                .leaveBalances(balances)
                .pendingLeaveCount(pendingLeave)
                .totalAssignedTasks(taskRepository.countByAssignedToId(employee.getId()))
                .completedTasks(taskRepository.countByAssignedToIdAndStatus(
                        employee.getId(), TaskStatus.COMPLETED))
                .unreadNotificationCount(notificationRepository
                        .countByUserIdAndIsReadFalse(employee.getUser().getId()))
                .build();
    }

    private Employee resolveEmployee(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));

        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));
    }
}