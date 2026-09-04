package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.admin.AdminOrderResponse;
import com.Siddhant.UserApp.dto.admin.AdminOrderStatisticsResponse;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentStatus;

import java.util.List;
import java.util.UUID;
public interface AdminOrderService {
    List<AdminOrderResponse> getAllOrders();
    AdminOrderResponse getOrderById(UUID orderId);
    List<AdminOrderResponse> searchOrders(String keyword);
    List<AdminOrderResponse> filterOrders(OrderStatus status, PaymentStatus paymentStatus);
    AdminOrderResponse updateOrderStatus(UUID orderId, OrderStatus status);
    AdminOrderResponse updatePaymentStatus(UUID orderId, PaymentStatus paymentStatus);
    AdminOrderResponse cancelOrder(UUID orderId);
    AdminOrderStatisticsResponse getOrderStatistics();
    List<AdminOrderResponse> getOrdersByFarmer(UUID farmerId);
}