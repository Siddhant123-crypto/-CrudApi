package com.Siddhant.UserApp.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleLoginResponse {
    private String message;
    private Object response;
    private String accessToken;
    private Long expiresIn;
}
