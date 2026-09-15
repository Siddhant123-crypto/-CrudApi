package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.Entity.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
@Data
@RequiredArgsConstructor
public class LoginData {
    private String name;
    private String email;
    private Role role;
    private UUID farmerId;
    private UUID userId;
    private String profilePhoto;
    private com.Siddhant.UserApp.Entity.Status status;
    private String blockReason;
    private java.time.LocalDateTime blockedUntil;
}