package com.Siddhant.UserApp.dto;
import lombok.Data;
@Data
public class OrderCancelResponse {
    private String message;
    private OrderResponse response;
    public OrderCancelResponse() {
    }
    public OrderCancelResponse(String message, OrderResponse response
    ) {this.message = message;this.response = response;
    }
}