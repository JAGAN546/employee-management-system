package com.employee.management.service.impl;

import com.employee.management.dto.NotificationResponseDto;
import com.employee.management.entity.Notification;
import com.employee.management.entity.NotificationType;
import com.employee.management.entity.User;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.repository.NotificationRepository;
import com.employee.management.repository.UserRepository;
import com.employee.management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponseDto> getMyNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "User not found with email: " + email));

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    @Override
    public NotificationResponseDto markAsRead(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "User not found with email: " + email));

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Notification not found with id: " + id));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only mark your own notifications as read");
        }

        notification.setRead(true);
        return toDto(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "User not found with email: " + email));

        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalse(user.getId());

        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationResponseDto toDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}