package com.Siddhant.UserApp.dto;

public class UserMappingResponse {

    private String message;
    private UserMappingRequest response;

    public UserMappingResponse() {
    }

    public UserMappingResponse(String message, UserMappingRequest response) {
        this.message = message;
        this.response = response;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserMappingRequest getResponse() {
        return response;
    }

    public void setResponse(UserMappingRequest response) {
        this.response = response;
    }
}

