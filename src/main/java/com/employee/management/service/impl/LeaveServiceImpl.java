package com.employee.management.service.impl;

import com.employee.management.dto.EmployeeSummaryDto;
import com.employee.management.dto.LeaveBalanceDto;
import com.employee.management.dto.LeaveRequestDto;
import com.employee.management.dto.LeaveResponseDto;
import com.employee.management.entity.*;
import com.employee.management.exception.EmployeeProfileNotLinkedException;
import com.employee.management.exception.InvalidLeaveException;
import com.employee.management.exception.LeaveNotFoundException;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.LeaveRepository;
import com.employee.management.repository.UserRepository;
import com.employee.management.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.employee.management.entity.NotificationType;
import com.employee.management.service.NotificationService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final Map<LeaveType, Integer> ANNUAL_QUOTAS = new EnumMap<>(LeaveType.class);
    static {
        ANNUAL_QUOTAS.put(LeaveType.CASUAL, 12);
        ANNUAL_QUOTAS.put(LeaveType.SICK, 10);
        ANNUAL_QUOTAS.put(LeaveType.EARNED, 15);
    }

    @Override
    public LeaveResponseDto applyLeave(String email, LeaveRequestDto requestDto) {
        Employee employee = resolveEmployee(email);

        if (requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new InvalidLeaveException("End date cannot be before start date");
        }

        List<Leave> activeLeaves = leaveRepository.findByEmployeeIdAndStatusIn(
                employee.getId(), List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED));

        boolean overlaps = activeLeaves.stream().anyMatch(existing ->
                !requestDto.getEndDate().isBefore(existing.getStartDate())
                        && !requestDto.getStartDate().isAfter(existing.getEndDate()));

        if (overlaps) {
            throw new InvalidLeaveException(
                    "You already have a pending or approved leave overlapping these dates");
        }

        long requestedDays = ChronoUnit.DAYS.between(
                requestDto.getStartDate(), requestDto.getEndDate()) + 1;

        if (ANNUAL_QUOTAS.containsKey(requestDto.getLeaveType())) {
            int used = usedDaysThisYear(employee.getId(), requestDto.getLeaveType());
            int quota = ANNUAL_QUOTAS.get(requestDto.getLeaveType());

            if (used + requestedDays > quota) {
                throw new InvalidLeaveException(
                        "Insufficient leave balance: " + (quota - used) +
                                " day(s) remaining for " + requestDto.getLeaveType());
            }
        }

        Leave leave = new Leave();
        leave.setEmployee(employee);
        leave.setLeaveType(requestDto.getLeaveType());
        leave.setStartDate(requestDto.getStartDate());
        leave.setEndDate(requestDto.getEndDate());
        leave.setReason(requestDto.getReason());

        Leave saved = leaveRepository.save(leave);
        return toDto(saved);
    }

    @Override
    public List<LeaveResponseDto> getMyLeaves(String email) {
        Employee employee = resolveEmployee(email);
        return leaveRepository.findByEmployeeIdOrderByAppliedAtDesc(employee.getId())
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<LeaveResponseDto> getAllLeaves() {
        return leaveRepository.findAllByOrderByAppliedAtDesc()
                .stream().map(this::toDto).toList();
    }

    @Override
    public LeaveResponseDto approveLeave(Long id, String actionedByEmail) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found with id: " + id));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveException("Only pending leave requests can be approved");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setActionedBy(actionedByEmail);
        leave.setActionedAt(java.time.LocalDateTime.now());

        Leave saved = leaveRepository.save(leave);

        notificationService.createNotification(
                leave.getEmployee().getUser(),
                "Your " + leave.getLeaveType() + " leave from " + leave.getStartDate() +
                        " to " + leave.getEndDate() + " has been approved",
                NotificationType.LEAVE_APPROVED);

        return toDto(saved);
    }

    @Override
    public LeaveResponseDto rejectLeave(Long id, String actionedByEmail) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found with id: " + id));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveException("Only pending leave requests can be rejected");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setActionedBy(actionedByEmail);
        leave.setActionedAt(java.time.LocalDateTime.now());

        Leave saved = leaveRepository.save(leave);

        notificationService.createNotification(
                leave.getEmployee().getUser(),
                "Your " + leave.getLeaveType() + " leave from " + leave.getStartDate() +
                        " to " + leave.getEndDate() + " has been rejected",
                NotificationType.LEAVE_REJECTED);

        return toDto(saved);
    }

    @Override
    public LeaveResponseDto cancelLeave(Long id, String email) {
        Employee employee = resolveEmployee(email);

        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found with id: " + id));

        if (!leave.getEmployee().getId().equals(employee.getId())) {
            throw new AccessDeniedException("You can only cancel your own leave requests");
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveException("Only pending leave requests can be cancelled");
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        return toDto(leaveRepository.save(leave));
    }

    @Override
    public List<LeaveBalanceDto> getMyLeaveBalance(String email) {
        Employee employee = resolveEmployee(email);

        return ANNUAL_QUOTAS.entrySet().stream()
                .map(entry -> {
                    int used = usedDaysThisYear(employee.getId(), entry.getKey());
                    return LeaveBalanceDto.builder()
                            .leaveType(entry.getKey())
                            .totalDays(entry.getValue())
                            .usedDays(used)
                            .remainingDays(entry.getValue() - used)
                            .build();
                })
                .toList();
    }

    private int usedDaysThisYear(Long employeeId, LeaveType leaveType) {
        int currentYear = LocalDate.now().getYear();

        return leaveRepository.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED)
                .stream()
                .filter(l -> l.getLeaveType() == leaveType)
                .filter(l -> l.getStartDate().getYear() == currentYear)
                .mapToInt(l -> (int) ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
                .sum();
    }

    private Employee resolveEmployee(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));

        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));
    }

    private LeaveResponseDto toDto(Leave leave) {
        Employee employee = leave.getEmployee();

        EmployeeSummaryDto employeeSummary = EmployeeSummaryDto.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .build();

        return LeaveResponseDto.builder()
                .id(leave.getId())
                .employee(employeeSummary)
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .appliedAt(leave.getAppliedAt())
                .actionedBy(leave.getActionedBy())
                .actionedAt(leave.getActionedAt())
                .build();
    }
}