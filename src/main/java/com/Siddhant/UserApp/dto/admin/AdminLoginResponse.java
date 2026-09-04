package com.Siddhant.UserApp.dto.admin;
import com.Siddhant.UserApp.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {
    private String message;
    private String token;
    private String role;
    private UUID adminId;
    private String name;
    private String email;
}
