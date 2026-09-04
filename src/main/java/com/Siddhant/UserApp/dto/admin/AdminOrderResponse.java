package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminOrderResponse {
    private UUID orderId;
    private UUID customerId;
    private String customerName;
    private String customerMobile;
    private String customerAddress;
    private List<AdminOrderItemResponse> items;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime orderDate;
}