package com.Siddhant.UserApp.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class PaymentResponse {
    private String message;
    private OrderResponse response;
}
