package com.Siddhant.UserApp.dto;
import lombok.Data;
@Data
public class OrderCreateResponse {
    private String message;
    private OrderResponse response;
    public OrderCreateResponse() {
    }
    public OrderCreateResponse(
            String message,
            OrderResponse response
    ) {this.message = message;this.response = response;
    }
}
