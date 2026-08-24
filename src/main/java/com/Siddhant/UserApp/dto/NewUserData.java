package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
@Data
@RequiredArgsConstructor
public class NewUserData {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private UUID userId;
    private UUID farmerId;
    private String profilePhoto;
}