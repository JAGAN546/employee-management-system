package com.employee.management.service.impl;

import com.employee.management.dto.LoginRequestDto;
import com.employee.management.dto.LoginResponseDto;
import com.employee.management.dto.RegisterRequestDto;
import com.employee.management.dto.RegisterResponseDto;
import com.employee.management.entity.User;
import com.employee.management.exception.UserAlreadyExistsException;
import com.employee.management.repository.UserRepository;
import com.employee.management.security.JwtService;
import com.employee.management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.employee.management.entity.Employee;
import com.employee.management.entity.Role;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;

    @Override
    public RegisterResponseDto register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UserAlreadyExistsException(
                    "A user with email " + requestDto.getEmail() + " already exists");
        }

        Employee employee = null;

        if (requestDto.getRole() == Role.EMPLOYEE) {
            employee = employeeRepository.findByEmail(requestDto.getEmail())
                    .orElseThrow(() -> new EmployeeNotFoundException(
                            "No employee record found with this email. " +
                                    "Please contact HR to be added before registering."));

            if (employee.getUser() != null) {
                throw new IllegalArgumentException(
                        "This employee record is already linked to a login account");
            }
        }

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(requestDto.getRole());

        User savedUser = userRepository.save(user);

        if (employee != null) {
            employee.setUser(savedUser);
            employeeRepository.save(employee);
        }

        return RegisterResponseDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(), requestDto.getPassword()));

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return LoginResponseDto.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}