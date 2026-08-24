package com.Siddhant.UserApp.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class CustomerOrderSummaryResponse {
    private long totalOrders;
    private long activeOrders;
    private long delivered;
    private long cancelled;
    private BigDecimal totalSpending;
}
