package com.Siddhant.UserApp.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class SendSupportMessageRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;
}
