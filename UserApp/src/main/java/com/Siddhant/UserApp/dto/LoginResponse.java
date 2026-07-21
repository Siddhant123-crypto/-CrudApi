package com.Siddhant.UserApp.dto;

public class LoginResponse {

    private String message;
    private LoginData response;


    public LoginResponse() {
    }


    public LoginResponse(String message, LoginData response) {
        this.message = message;
        this.response = response;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public LoginData getResponse() {
        return response;
    }


    public void setResponse(LoginData response) {
        this.response = response;
    }
}