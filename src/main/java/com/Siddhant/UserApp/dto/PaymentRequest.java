package com.Siddhant.UserApp.dto;

import com.Siddhant.UserApp.enums.PaymentMethod;
import com.Siddhant.UserApp.enums.PaymentStatus;
import lombok.Data;
@Data
public class PaymentRequest {
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String transactionId;
}
