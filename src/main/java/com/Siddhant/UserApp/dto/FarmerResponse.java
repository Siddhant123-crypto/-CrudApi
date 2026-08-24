package com.Siddhant.UserApp.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarmerResponse {
    private UUID farmerId;
    private String name;
    private String village;
    private String address;
    private String postalCode;
    private String mobile;
    private String email;
    private String state;
    private String profilePhoto;
    private String createdBy;
    private LocalDateTime createdOn;
    private String updatedBy;
    private LocalDateTime updatedOn;
    private Boolean isActive;
    private Boolean isDelete;
    private String status;
    private String accessToken;
    private Long expiresIn;
}