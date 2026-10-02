package com.employee.management.service;

import com.employee.management.dto.DepartmentRequestDto;
import com.employee.management.dto.DepartmentResponseDto;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDto createDepartment(DepartmentRequestDto requestDto);
    List<DepartmentResponseDto> getAllDepartments();
    DepartmentResponseDto getDepartmentById(Long id);
}