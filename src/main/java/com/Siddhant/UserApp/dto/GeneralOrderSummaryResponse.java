package com.Siddhant.UserApp.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class GeneralOrderSummaryResponse {
    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private BigDecimal totalAmount;
}
