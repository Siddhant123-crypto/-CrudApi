package com.Siddhant.UserApp.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
@Data
public class OrderItemResponse {
    private UUID productId;
    private String productName;
    private String imageUrl;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal price;
    private BigDecimal subtotal;
}
