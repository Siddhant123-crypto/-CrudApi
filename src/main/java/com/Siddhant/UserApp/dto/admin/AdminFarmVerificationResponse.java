package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminFarmVerificationResponse {
    private UUID farmerId;
    private String farmerName;
    private Boolean verified;
    private String verificationMessage;
    private LocalDateTime verificationRequestedOn;
}