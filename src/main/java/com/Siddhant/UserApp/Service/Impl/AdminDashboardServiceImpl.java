package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.AdminDashboardService;
import com.Siddhant.UserApp.dto.admin.AdminDashboardResponse;
import com.Siddhant.UserApp.dto.admin.AdminRecentOrderResponse;
import com.Siddhant.UserApp.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import com.Siddhant.UserApp.dto.admin.AdminRecentRegistrationResponse;
import java.util.Arrays;
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    @Override
    public AdminDashboardResponse getDashboardSummary() {
        long totalFarmers = userRepository.countByRole(Role.FARMER);
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long completedOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        BigDecimal totalRevenue = orderRepository.getTotalOrderAmount();
        if (totalRevenue == null) {totalRevenue = BigDecimal.ZERO;}
        long pendingFarmers = userRepository.countByRoleAndStatus(Role.FARMER, Status.PENDING);
        long activeFarmers = userRepository.countByRoleAndIsActiveTrue(Role.FARMER);
        long blockedFarmers = userRepository.countByRoleAndIsActiveFalse(Role.FARMER);
        long activeCustomers = userRepository.countByRoleAndIsActiveTrue(Role.CUSTOMER);
        long blockedCustomers = userRepository.countByRoleAndIsActiveFalse(Role.CUSTOMER);
        List<AdminRecentOrderResponse> recentOrders = orderRepository.findTop5ByOrderByCreatedOnDesc().stream().map(order -> new AdminRecentOrderResponse(
                                order.getOrderId(), order.getOrderNumber(), order.getCustomerName(), order.getTotalAmount(), order.getStatus(), order.getPaymentStatus(), order.getCreatedOn()
                        )).toList();
        List<AdminRecentRegistrationResponse> recentRegistrations = userRepository.findTop5ByRoleInOrderByCreatedOnDesc(Arrays.asList(Role.FARMER, Role.CUSTOMER)).stream().map(user -> new AdminRecentRegistrationResponse(
                                user.getUserId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedOn()
                        )).toList();
        return new AdminDashboardResponse(totalFarmers, totalCustomers, totalProducts, totalOrders, pendingFarmers, activeFarmers, blockedFarmers, activeCustomers, blockedCustomers, pendingOrders, completedOrders, cancelledOrders, totalRevenue, recentOrders,recentRegistrations);
    }
}