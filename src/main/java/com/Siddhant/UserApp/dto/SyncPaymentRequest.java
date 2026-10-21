package com.Siddhant.UserApp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncPaymentRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;
}
