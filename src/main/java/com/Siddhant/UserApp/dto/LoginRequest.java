package com.Siddhant.UserApp.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email")
    private String email;
    private String password;
}
