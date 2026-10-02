package com.employee.management.service.impl;

import com.employee.management.dto.AttendanceResponseDto;
import com.employee.management.entity.Attendance;
import com.employee.management.entity.AttendanceStatus;
import com.employee.management.entity.Employee;
import com.employee.management.entity.User;
import com.employee.management.exception.AlreadyCheckedInException;
import com.employee.management.exception.AlreadyCheckedOutException;
import com.employee.management.exception.EmployeeProfileNotLinkedException;
import com.employee.management.exception.NotCheckedInException;
import com.employee.management.repository.AttendanceRepository;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.UserRepository;
import com.employee.management.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    private static final LocalTime LATE_THRESHOLD = LocalTime.of(10, 0);
    private static final double HALF_DAY_THRESHOLD_HOURS = 4.0;

    @Override
    public AttendanceResponseDto checkIn(String email) {
        Employee employee = resolveEmployee(email);
        LocalDate today = LocalDate.now();

        attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today)
                .ifPresent(a -> {
                    throw new AlreadyCheckedInException(
                            "You have already checked in today");
                });

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(today);
        attendance.setCheckInTime(LocalTime.now());

        Attendance saved = attendanceRepository.save(attendance);
        return toDto(saved);
    }

    @Override
    public AttendanceResponseDto checkOut(String email) {
        Employee employee = resolveEmployee(email);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDate(employee.getId(), today)
                .orElseThrow(() -> new NotCheckedInException(
                        "You have not checked in today"));

        if (attendance.getCheckOutTime() != null) {
            throw new AlreadyCheckedOutException(
                    "You have already checked out today");
        }

        LocalTime checkOutTime = LocalTime.now();
        attendance.setCheckOutTime(checkOutTime);

        double hoursWorked = Duration.between(
                attendance.getCheckInTime(), checkOutTime).toMinutes() / 60.0;
        attendance.setWorkingHours(Math.round(hoursWorked * 100.0) / 100.0);

        attendance.setStatus(determineStatus(attendance.getCheckInTime(), hoursWorked));

        Attendance saved = attendanceRepository.save(attendance);
        return toDto(saved);
    }

    @Override
    public List<AttendanceResponseDto> getMyHistory(String email) {
        Employee employee = resolveEmployee(email);

        return attendanceRepository.findByEmployeeIdOrderByDateDesc(employee.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    private Employee resolveEmployee(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));

        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));
    }

    private AttendanceStatus determineStatus(LocalTime checkInTime, double hoursWorked) {
        if (hoursWorked < HALF_DAY_THRESHOLD_HOURS) {
            return AttendanceStatus.HALF_DAY;
        }
        if (checkInTime.isAfter(LATE_THRESHOLD)) {
            return AttendanceStatus.LATE;
        }
        return AttendanceStatus.PRESENT;
    }

    private AttendanceResponseDto toDto(Attendance attendance) {
        return AttendanceResponseDto.builder()
                .id(attendance.getId())
                .date(attendance.getDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .status(attendance.getStatus())
                .workingHours(attendance.getWorkingHours())
                .build();
    }
}