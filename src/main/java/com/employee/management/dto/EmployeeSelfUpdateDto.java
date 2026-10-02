package com.employee.management.dto;

import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeSelfUpdateDto {

    private String phone;

    private String address;

    private String gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
}