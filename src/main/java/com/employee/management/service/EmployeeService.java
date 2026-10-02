package com.employee.management.service;

import com.employee.management.dto.EmployeeRequestDto;
import com.employee.management.dto.EmployeeResponseDto;
import com.employee.management.dto.EmployeeSelfUpdateDto;
import com.employee.management.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto);

    EmployeeResponseDto getEmployeeById(Long id);

    Page<EmployeeResponseDto> getAllEmployees(
            String keyword, String department, EmployeeStatus status, Pageable pageable);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto);

    void deactivateEmployee(Long id);

    EmployeeResponseDto getMyProfile(String email);

    EmployeeResponseDto updateMyProfile(String email, EmployeeSelfUpdateDto requestDto);
}