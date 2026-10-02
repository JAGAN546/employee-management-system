package com.employee.management.repository;

import com.employee.management.entity.Leave;
import com.employee.management.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployeeIdOrderByAppliedAtDesc(Long employeeId);

    List<Leave> findAllByOrderByAppliedAtDesc();

    List<Leave> findByEmployeeIdAndStatusIn(Long employeeId, List<LeaveStatus> statuses);

    List<Leave> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    long countByStatus(LeaveStatus status);

    @Query("SELECT COUNT(DISTINCT l.employee.id) FROM Leave l " +
            "WHERE l.status = 'APPROVED' AND :today BETWEEN l.startDate AND l.endDate")
    long countEmployeesOnLeaveToday(@Param("today") LocalDate today);
}