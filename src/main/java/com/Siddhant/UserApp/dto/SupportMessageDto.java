package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.Entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class SupportMessageDto {
    private UUID supportMessageId;
    private UUID supportRequestId;
    private UUID senderId;
    private Role senderType;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
