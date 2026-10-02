package com.employee.management.service.impl;

import com.employee.management.entity.Department;
import com.employee.management.exception.DepartmentNotFoundException;
import com.employee.management.repository.DepartmentRepository;
import com.employee.management.dto.EmployeeRequestDto;
import com.employee.management.dto.EmployeeResponseDto;
import com.employee.management.entity.Employee;
import com.employee.management.entity.EmployeeStatus;
import com.employee.management.exception.DuplicateEmailException;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.mapper.EmployeeMapper;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.employee.management.entity.User;
import com.employee.management.exception.EmployeeProfileNotLinkedException;
import com.employee.management.repository.UserRepository;
import com.employee.management.dto.EmployeeSelfUpdateDto;
import com.employee.management.specification.EmployeeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        if (employeeRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateEmailException(
                    "An employee with email " + requestDto.getEmail() + " already exists");
        }
        if (employeeRepository.findByEmployeeCode(requestDto.getEmployeeCode()).isPresent()) {
            throw new DuplicateEmailException(
                    "An employee with code " + requestDto.getEmployeeCode() + " already exists");
        }

        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Department not found with id: " + requestDto.getDepartmentId()));

        Employee employee = EmployeeMapper.toEntity(requestDto);
        employee.setDepartment(department);

        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDto(savedEmployee);
    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with id: " + id));

        return EmployeeMapper.toDto(employee);
    }

    @Override
    public Page<EmployeeResponseDto> getAllEmployees(
            String keyword, String department, EmployeeStatus status, Pageable pageable) {

        return employeeRepository
                .findAll(EmployeeSpecification.filterBy(keyword, department, status), pageable)
                .map(EmployeeMapper::toDto);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with id: " + id));

        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Department not found with id: " + requestDto.getDepartmentId()));

        employee.setFirstName(requestDto.getFirstName());
        employee.setLastName(requestDto.getLastName());
        employee.setPhone(requestDto.getPhone());
        employee.setDateOfBirth(requestDto.getDateOfBirth());
        employee.setGender(requestDto.getGender());
        employee.setAddress(requestDto.getAddress());
        employee.setJoiningDate(requestDto.getJoiningDate());
        employee.setDesignation(requestDto.getDesignation());
        employee.setSalary(requestDto.getSalary());
        employee.setDepartment(department);
        // Note: email and employeeCode are intentionally NOT updated here — see explanation below

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDto(updatedEmployee);
    }

    @Override
    public void deactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with id: " + id));

        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
    }

    @Override
    public EmployeeResponseDto getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "User not found with email: " + email));

        Employee employee = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));

        return EmployeeMapper.toDto(employee);
    }

    @Override
    public EmployeeResponseDto updateMyProfile(String email, EmployeeSelfUpdateDto requestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "User not found with email: " + email));

        Employee employee = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));

        employee.setPhone(requestDto.getPhone());
        employee.setAddress(requestDto.getAddress());
        employee.setGender(requestDto.getGender());
        employee.setDateOfBirth(requestDto.getDateOfBirth());

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDto(updatedEmployee);
    }
}