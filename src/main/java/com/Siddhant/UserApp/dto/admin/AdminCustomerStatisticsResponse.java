package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class AdminCustomerStatisticsResponse {
    private Long totalOrders;
    private Long pendingOrders;
    private Long confirmedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private BigDecimal totalSpent;
}