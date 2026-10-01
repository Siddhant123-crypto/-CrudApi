package com.Siddhant.UserApp.dto.admin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class AdminLoginRequest {
    @NotBlank(message = "Email or Mobile is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
