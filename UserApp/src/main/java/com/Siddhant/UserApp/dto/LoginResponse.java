package com.Siddhant.UserApp.dto;

public class LoginResponse {

    private String message;
    private LoginRequest response;

    public LoginResponse() {
    }

    public LoginResponse(String message, LoginRequest response) {
        this.message = message;
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoginRequest getResponse() {
        return response;
    }

    public void setResponse(LoginRequest response) {
        this.response = response;
    }
}