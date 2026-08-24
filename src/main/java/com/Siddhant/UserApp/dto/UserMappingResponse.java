package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class UserMappingResponse {
    private String message;
    private UserMappingRequest response;
    public UserMappingResponse(String message, UserMappingRequest response) {
        this.message = message;
        this.response = response;
    }
}

