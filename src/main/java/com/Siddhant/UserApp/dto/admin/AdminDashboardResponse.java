package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalFarmers;
    private long totalCustomers;
    private long totalProducts;
    private long totalOrders;
    private Long pendingFarmers;
    private Long activeFarmers;
    private Long blockedFarmers;
    private Long activeCustomers;
    private Long blockedCustomers;
    private long pendingOrders;
    private long completedOrders;
    private long cancelledOrders;
    private BigDecimal totalRevenue;
    private List<?> recentOrders;
    private List<?> recentRegistrations;
}