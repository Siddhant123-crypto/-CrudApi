package com.Siddhant.UserApp.dto.admin;

import lombok.Data;

@Data
public class BlockFarmerRequest {
    private String reason;
    private Integer durationDays;
}
