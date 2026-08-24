package com.Siddhant.UserApp.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class FarmerOrderSummaryResponse {
    private long totalOrders;
    private long newOrders;
    private long preparing;
    private long ready;
    private long outForDelivery;
    private long delivered;
    private BigDecimal totalSales;
}