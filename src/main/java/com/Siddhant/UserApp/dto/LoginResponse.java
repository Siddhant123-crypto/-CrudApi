package com.Siddhant.UserApp.dto;

public class LoginResponse {

    private String message;
    private LoginData response;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private Integer refreshExpiresIn;


    public LoginResponse() {
    }


    public LoginResponse(String message, LoginData response, String accessToken) {
        this.message = message;
        this.response = response;
        this.accessToken = accessToken;
        this.refreshToken = null;
        this.expiresIn = null;
        this.refreshExpiresIn = null;
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

    public String getAccessToken(){
        return  accessToken;
    }
    public void setAccessToken(String accessToken){
        this.accessToken=accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Integer getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(Integer refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }
}