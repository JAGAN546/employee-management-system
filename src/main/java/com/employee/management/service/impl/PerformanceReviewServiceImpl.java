package com.employee.management.service.impl;

import com.employee.management.dto.EmployeeSummaryDto;
import com.employee.management.dto.PerformanceReviewRequestDto;
import com.employee.management.dto.PerformanceReviewResponseDto;
import com.employee.management.entity.Employee;
import com.employee.management.entity.PerformanceReview;
import com.employee.management.entity.User;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.exception.EmployeeProfileNotLinkedException;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.PerformanceReviewRepository;
import com.employee.management.repository.UserRepository;
import com.employee.management.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    public PerformanceReviewResponseDto createReview(
            PerformanceReviewRequestDto requestDto, String reviewerEmail) {

        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with id: " + requestDto.getEmployeeId()));

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setReviewedBy(reviewerEmail);
        review.setReviewPeriod(requestDto.getReviewPeriod());
        review.setRating(requestDto.getRating());
        review.setStrengths(requestDto.getStrengths());
        review.setImprovements(requestDto.getImprovements());
        review.setComments(requestDto.getComments());

        PerformanceReview saved = performanceReviewRepository.save(review);
        return toDto(saved);
    }

    @Override
    public List<PerformanceReviewResponseDto> getMyReviews(String email) {
        Employee employee = resolveEmployee(email);
        return performanceReviewRepository.findByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<PerformanceReviewResponseDto> getAllReviews() {
        return performanceReviewRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).toList();
    }

    private Employee resolveEmployee(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));

        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EmployeeProfileNotLinkedException(
                        "No employee profile is linked to this account"));
    }

    private PerformanceReviewResponseDto toDto(PerformanceReview review) {
        Employee employee = review.getEmployee();

        EmployeeSummaryDto summary = EmployeeSummaryDto.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .build();

        return PerformanceReviewResponseDto.builder()
                .id(review.getId())
                .employee(summary)
                .reviewedBy(review.getReviewedBy())
                .reviewPeriod(review.getReviewPeriod())
                .rating(review.getRating())
                .strengths(review.getStrengths())
                .improvements(review.getImprovements())
                .comments(review.getComments())
                .createdAt(review.getCreatedAt())
                .build();
    }
}