package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class AdminOrderStatisticsResponse {
    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private long paidOrders;
    private long pendingPaymentOrders;
    private BigDecimal totalOrderAmount;
}