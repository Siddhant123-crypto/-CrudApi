package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.NotificationResponse;
import com.Siddhant.UserApp.enums.NotificationType;
import java.util.List;
import java.util.UUID;
public interface NotificationService {
    NotificationResponse createNotification(
            UUID userId,
            String title,
            String message,
            NotificationType notificationType
    );
    NotificationResponse createAdminNotification(
            UUID adminId,
            String title,
            String message,
            NotificationType notificationType
    );
    List<NotificationResponse> getMyNotifications();
    List<NotificationResponse> getMyUnreadNotifications();
    long getMyUnreadCount();
    NotificationResponse markAsRead(UUID notificationId);
    void markAllAsRead();
    Object sendNotification(com.Siddhant.UserApp.dto.NotificationSendRequest request);
    List<com.Siddhant.UserApp.dto.NotificationHistoryResponse> getMySentNotifications();
    void deleteNotification(UUID notificationId);
}