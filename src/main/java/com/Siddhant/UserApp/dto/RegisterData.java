package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.Status;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
@Data
@RequiredArgsConstructor
public class RegisterData {
    private UUID userId;
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
    private String createdBy;
    private LocalDateTime createdOn;
    private String updatedBy;
    private LocalDateTime updatedOn;
    private Boolean isActive;
    private Boolean isDelete;
    private Status status;
    private String profilePhoto;
}