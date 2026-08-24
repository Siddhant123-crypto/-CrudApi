package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckUserResponse {
    private boolean exists;
    private String message;
    private Role role;
}
