package com.recruitment.service;

import com.recruitment.model.Notification;
import com.recruitment.model.User;
import com.recruitment.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(User user, String title, String message, String type, String link) {
        Notification notification = new Notification(user, title, message, type, link);
        notificationRepository.save(notification);
    }

    public Page<Notification> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<Notification> getLatestNotifications(Long userId, int limit) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit)).getContent();
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead(Long userId) {
        Page<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(
            userId, Pageable.unpaged());
        unread.forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
