package com.Siddhant.UserApp.dto.admin;

import lombok.Data;

@Data
public class BlockCustomerRequest {
    private String reason;
    private Integer durationDays;
}
