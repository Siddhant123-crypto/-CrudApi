package com.Siddhant.UserApp.dto;

import com.Siddhant.UserApp.enums.SupportRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SupportRequestResponse {
    private UUID supportRequestId;
    private UUID userId;
    private String userRole;
    private String userName;
    private String subject;
    private String description;
    private SupportRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
