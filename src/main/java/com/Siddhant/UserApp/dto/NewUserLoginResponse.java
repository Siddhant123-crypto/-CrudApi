package com.Siddhant.UserApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class NewUserLoginResponse {
    private String message;
    private NewUserData response;
    private String accessToken;
    private Long expiresIn;
    public NewUserLoginResponse(String message, NewUserData response) {
        this.message = message;
        this.response = response;
    }
}