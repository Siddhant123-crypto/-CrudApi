package com.Siddhant.UserApp.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
public class CartResponse {
    private UUID cartId;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private List<CartItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
