package com.Siddhant.UserApp.dto.admin;
import com.Siddhant.UserApp.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminRecentRegistrationResponse {
    private UUID userId;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime registeredOn;
}