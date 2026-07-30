package com.Siddhant.UserApp.dto;


import lombok.Data;


@Data
public class ForgotPasswordRequest {


    private String email;

    private String password;

    private String confirmPassword;


}
