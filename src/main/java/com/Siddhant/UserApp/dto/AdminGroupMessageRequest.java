package com.Siddhant.UserApp.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminGroupMessageRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;
}
