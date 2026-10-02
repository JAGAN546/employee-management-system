package com.employee.management.controller;

import com.employee.management.dto.PerformanceReviewRequestDto;
import com.employee.management.dto.PerformanceReviewResponseDto;
import com.employee.management.service.PerformanceReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping
    public ResponseEntity<PerformanceReviewResponseDto> createReview(
            @Valid @RequestBody PerformanceReviewRequestDto requestDto,
            Authentication authentication) {
        PerformanceReviewResponseDto response =
                performanceReviewService.createReview(requestDto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<PerformanceReviewResponseDto>> getAllReviews() {
        return ResponseEntity.ok(performanceReviewService.getAllReviews());
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<PerformanceReviewResponseDto>> getMyReviews(
            Authentication authentication) {
        return ResponseEntity.ok(performanceReviewService.getMyReviews(authentication.getName()));
    }
}