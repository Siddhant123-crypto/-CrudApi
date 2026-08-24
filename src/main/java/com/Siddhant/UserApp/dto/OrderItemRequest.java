package com.Siddhant.UserApp.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
@Data
public class OrderItemRequest {
    private UUID productId;
    private BigDecimal quantity;
}