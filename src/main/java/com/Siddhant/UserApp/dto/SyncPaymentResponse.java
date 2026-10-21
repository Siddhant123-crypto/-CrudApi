package com.Siddhant.UserApp.dto;

import com.Siddhant.UserApp.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncPaymentResponse {

    private UUID orderId;
    private PaymentStatus paymentStatus;
    private String razorpayPaymentId;
    private boolean paid;
}
