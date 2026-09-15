package com.Siddhant.UserApp.dto;

import com.Siddhant.UserApp.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHistoryResponse {
    private UUID notificationId;
    private String title;
    private String message;
    private NotificationType notificationType;
    private LocalDateTime createdAt;
    
    private UUID senderId;
    private String senderType;
    
    private UUID receiverId;
    private String receiverName;
    private String receiverType;
}
