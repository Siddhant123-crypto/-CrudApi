package com.Siddhant.UserApp.dto;

public class NewUserLoginResponse {

    private String message;
    private NewUserData response;

    public NewUserLoginResponse() {
    }

    public NewUserLoginResponse(String message, NewUserData response) {
        this.message = message;
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NewUserData getResponse() {
        return response;
    }

    public void setResponse(NewUserData response) {
        this.response = response;
    }
}
