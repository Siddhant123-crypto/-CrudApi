package com.Siddhant.UserApp.dto;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
@Data
@RequiredArgsConstructor
public class CheckUserRequest {
    @NotBlank(message = "Email or Mobile is required")
    private String identifier;
}
