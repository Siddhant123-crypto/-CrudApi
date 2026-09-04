package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminFarmerSalesResponse {
    private UUID farmerId;
    private String farmerName;
    private Long totalOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private Double totalProductsSold;
    private BigDecimal totalRevenue;
}