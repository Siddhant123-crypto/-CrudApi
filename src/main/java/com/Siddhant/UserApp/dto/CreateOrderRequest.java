package com.Siddhant.UserApp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotNull(message = "Order ID cannot be null")
    private UUID orderId;
}
