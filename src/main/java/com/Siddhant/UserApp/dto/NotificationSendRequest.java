package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.enums.NotificationType;
import lombok.Data;

import java.util.UUID;
@Data
public class NotificationSendRequest {
    private String receiverType;
    private UUID receiverId;
    private String title;
    private String message;
    private NotificationType notificationType;
}
