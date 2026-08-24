package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Data
public class UpdateResponse {
    private String message;
    private RegisterData response;

    public UpdateResponse(String userNotFound, Object o) {
    }
}
