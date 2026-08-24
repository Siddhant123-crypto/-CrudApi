package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class LoginResponse {
    private String message;
    private LoginData response;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private Integer refreshExpiresIn;
    public LoginResponse(String message, LoginData response, String accessToken) {
        this.message = message;
        this.response = response;
        this.accessToken = accessToken;
        this.refreshToken = null;
        this.expiresIn = null;
        this.refreshExpiresIn = null;
    }
}