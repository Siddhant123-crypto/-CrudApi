package com.Siddhant.UserApp.dto;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
@Data
@RequiredArgsConstructor
public class FarmerRequest {
    private String name;
    private String village;
    private String address;
    private String postalCode;
    private String mobile;
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email")
    private String email;
    private String state;
    private String password;
    private String profilePhoto;
    private String createdBy;
    private String updatedBy;
    private Boolean isActive;
    private Boolean isDelete;
    private String status;
}