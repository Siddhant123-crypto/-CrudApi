package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminOrderItemResponse {
    private UUID productId;
    private String productName;
    private String productPhoto;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal price;
    private BigDecimal subtotal;
    private UUID farmerId;
    private String farmerName;
}