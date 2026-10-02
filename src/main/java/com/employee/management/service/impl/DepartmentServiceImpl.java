package com.employee.management.service.impl;

import com.employee.management.dto.DepartmentRequestDto;
import com.employee.management.dto.DepartmentResponseDto;
import com.employee.management.entity.Department;
import com.employee.management.exception.DepartmentNotFoundException;
import com.employee.management.repository.DepartmentRepository;
import com.employee.management.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentResponseDto createDepartment(DepartmentRequestDto requestDto) {
        if (departmentRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException(
                    "Department with name " + requestDto.getName() + " already exists");
        }

        Department department = new Department();
        department.setName(requestDto.getName());
        department.setDescription(requestDto.getDescription());

        Department saved = departmentRepository.save(department);
        return toDto(saved);
    }

    @Override
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public DepartmentResponseDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Department not found with id: " + id));
        return toDto(department);
    }

    private DepartmentResponseDto toDto(Department department) {
        return DepartmentResponseDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt())
                .build();
    }
}