package com.Siddhant.UserApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class VerifyPaymentRequest {
    @NotNull(message = "Order ID cannot be null")
    private UUID orderId;

    @NotBlank(message = "Razorpay Order ID is required")
    private String razorpay_order_id;

    @NotBlank(message = "Razorpay Payment ID is required")
    private String razorpay_payment_id;

    @NotBlank(message = "Razorpay Signature is required")
    private String razorpay_signature;
}
