package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@Data
@RequiredArgsConstructor
public class NewUserLoginRequest {
    private String email;
    private String password;
}
