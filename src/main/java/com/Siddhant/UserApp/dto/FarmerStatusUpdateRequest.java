package com.Siddhant.UserApp.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerStatusUpdateRequest {
    private Boolean isActive;
    private String inactiveReason;
}
