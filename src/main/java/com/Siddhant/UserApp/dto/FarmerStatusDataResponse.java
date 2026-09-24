package com.Siddhant.UserApp.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarmerStatusDataResponse {
    private Boolean isActive;
    private String inactiveReason;
    private LocalDateTime inactiveSince;
}
