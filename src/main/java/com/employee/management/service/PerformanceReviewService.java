package com.employee.management.service;

import com.employee.management.dto.PerformanceReviewRequestDto;
import com.employee.management.dto.PerformanceReviewResponseDto;

import java.util.List;

public interface PerformanceReviewService {
    PerformanceReviewResponseDto createReview(PerformanceReviewRequestDto requestDto, String reviewerEmail);
    List<PerformanceReviewResponseDto> getMyReviews(String email);
    List<PerformanceReviewResponseDto> getAllReviews();
}