package com.Siddhant.UserApp.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class SupportRequestRequest {
    @NotBlank(message = "Subject is required")
    private String subject;
    @NotBlank(message = "Description is required")
    private String description;
}
