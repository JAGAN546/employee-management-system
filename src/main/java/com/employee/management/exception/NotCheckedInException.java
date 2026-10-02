package com.employee.management.exception;

public class NotCheckedInException extends RuntimeException {
    public NotCheckedInException(String message) {
        super(message);
    }
}