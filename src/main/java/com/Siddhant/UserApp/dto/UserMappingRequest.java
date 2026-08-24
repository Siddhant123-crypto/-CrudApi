package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.Entity.Status;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@RequiredArgsConstructor
public class UserMappingRequest {
    private UUID userId;
    private String name;
    private String address;
    private String pincode;
    private String mobileNo;
    private String city;
    private String state;
    private String createdBy;
    private LocalDateTime createdOn;
    private String updatedBy;
    private LocalDateTime updatedOn;
    private Boolean isActive;
    private Boolean isDelete;
    private Status status;
}