package com.employee.management.service;

import com.employee.management.dto.LoginRequestDto;
import com.employee.management.dto.LoginResponseDto;
import com.employee.management.dto.RegisterRequestDto;
import com.employee.management.dto.RegisterResponseDto;

public interface AuthService {
    RegisterResponseDto register(RegisterRequestDto requestDto);
    LoginResponseDto login(LoginRequestDto requestDto);
}