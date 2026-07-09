package com.Siddhant.UserApp.dto;

public class RegisterResponse {

    private String message;
    private RegisterData response;

    public RegisterResponse() {
    }

    public RegisterResponse(String message, RegisterData response) {
        this.message = message;
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RegisterData getResponse() {
        return response;
    }

    public void setResponse(RegisterData response) {
        this.response = response;
    }
}