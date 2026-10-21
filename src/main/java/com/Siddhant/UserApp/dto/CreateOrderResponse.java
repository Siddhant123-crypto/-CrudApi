package com.Siddhant.UserApp.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CreateOrderResponse {
    private String razorpayOrderId;
    private long amount;
    private String currency;
    private String keyId;
}
