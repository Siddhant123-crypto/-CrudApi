package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminFarmVerificationListResponse {
    private UUID farmerId;
    private String farmerName;
    private String farmName;
    private String status; // PENDING, VERIFIED, REJECTED
    private LocalDateTime verificationRequestedOn;
}
