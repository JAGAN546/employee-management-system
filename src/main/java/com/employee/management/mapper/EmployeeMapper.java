package com.employee.management.mapper;

import com.employee.management.dto.DepartmentSummaryDto;
import com.employee.management.dto.EmployeeRequestDto;
import com.employee.management.dto.EmployeeResponseDto;
import com.employee.management.entity.Employee;
import com.employee.management.entity.EmployeeStatus;

public class EmployeeMapper {

    public static Employee toEntity(EmployeeRequestDto dto) {
        Employee employee = new Employee();
        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setGender(dto.getGender());
        employee.setAddress(dto.getAddress());
        employee.setJoiningDate(dto.getJoiningDate());
        employee.setDesignation(dto.getDesignation());
        employee.setSalary(dto.getSalary());
        employee.setStatus(EmployeeStatus.ACTIVE);
        // department is intentionally NOT set here — see EmployeeServiceImpl
        return employee;
    }

    public static EmployeeResponseDto toDto(Employee employee) {
        DepartmentSummaryDto departmentSummary = DepartmentSummaryDto.builder()
                .id(employee.getDepartment().getId())
                .name(employee.getDepartment().getName())
                .build();

        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .address(employee.getAddress())
                .joiningDate(employee.getJoiningDate())
                .designation(employee.getDesignation())
                .salary(employee.getSalary())
                .status(employee.getStatus())
                .department(departmentSummary)
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}