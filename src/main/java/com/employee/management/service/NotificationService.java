package com.employee.management.service;

import com.employee.management.dto.NotificationResponseDto;
import com.employee.management.entity.NotificationType;
import com.employee.management.entity.User;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, String message, NotificationType type);
    List<NotificationResponseDto> getMyNotifications(String email);
    NotificationResponseDto markAsRead(Long id, String email);
    void markAllAsRead(String email);
}