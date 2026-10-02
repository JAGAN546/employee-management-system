package com.employee.management.exception;

public class EmployeeProfileNotLinkedException extends RuntimeException {
    public EmployeeProfileNotLinkedException(String message) {
        super(message);
    }
}