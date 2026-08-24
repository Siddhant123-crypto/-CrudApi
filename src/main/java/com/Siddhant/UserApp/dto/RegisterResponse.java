package com.Siddhant.UserApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterResponse {

    private String message;
    private Object response;
    private String accessToken;
    private long expiresIn;

    public RegisterResponse(String message, Object response) {
        this.message = message;
        this.response = response;
    }

    public RegisterResponse(
            String message,
            RegisterData response,
            String accessToken,
            long expiresIn) {

        this.message = message;
        this.response = response;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}