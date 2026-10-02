package com.employee.management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRequestDto {

    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    private String phone;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    private String designation;

    @Positive(message = "Salary must be a positive number")
    private Double salary;
}