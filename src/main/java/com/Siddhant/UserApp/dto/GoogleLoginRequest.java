package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@Data
@RequiredArgsConstructor
public class GoogleLoginRequest {
    private String idToken;
}
