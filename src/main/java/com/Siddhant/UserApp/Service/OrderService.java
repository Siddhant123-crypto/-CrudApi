package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.dto.CustomerOrderSummaryResponse;
import com.Siddhant.UserApp.dto.FarmerCancelOrderResponse;
import com.Siddhant.UserApp.dto.FarmerDashboardSummaryResponse;
import com.Siddhant.UserApp.dto.FarmerOrderSummaryResponse;
import com.Siddhant.UserApp.dto.GeneralOrderSummaryResponse;
import com.Siddhant.UserApp.dto.OrderRequest;
import com.Siddhant.UserApp.dto.OrderResponse;
import com.Siddhant.UserApp.dto.PaymentRequest;
import com.Siddhant.UserApp.enums.OrderStatus;

import java.util.List;
import java.util.UUID;
import com.Siddhant.UserApp.dto.TrackingLocationRequest;
import com.Siddhant.UserApp.dto.TrackingLocationResponse;

public interface OrderService {

    // CREATE ORDER
    OrderResponse createOrder(
            OrderRequest request
    );

    // CUSTOMER ORDER DETAILS
    OrderResponse getOrderById(
            UUID orderId
    );

    // CUSTOMER ACTIVE ORDERS
    List<OrderResponse> getCustomerMyOrders();

    // CUSTOMER ORDER HISTORY
    List<OrderResponse> getCustomerOrderHistory();

    // CUSTOMER ORDER SUMMARY
    CustomerOrderSummaryResponse getCustomerOrderSummary();

    // FARMER ORDERS
    List<OrderResponse> getFarmerOrders();

    // FARMER ORDERS BY STATUS
    List<OrderResponse> getFarmerOrdersByStatus(
            OrderStatus status
    );

    long getFarmerNewOrdersCount();

    void markFarmerOrdersAsSeen();

    // FARMER ACCEPT ORDER
    OrderResponse acceptOrder(
            UUID orderId
    );

    // UPDATE ORDER STATUS
    OrderResponse updateOrderStatus(
            UUID orderId,
            OrderStatus status
    );

    // CUSTOMER CANCEL ORDER
    OrderResponse cancelOrder(
            UUID orderId,
            String reason
    );

    // UPDATE CANCELLATION REASON
    OrderResponse updateCancellationReason(
            UUID orderId,
            String reason
    );

    // FARMER CANCEL ORDER
    FarmerCancelOrderResponse farmerCancelOrder(
            UUID orderId,
            String reason
    );

    // UPDATE PAYMENT
    OrderResponse updatePayment(
            UUID orderId,
            PaymentRequest request
    );

    // FARMER DASHBOARD SUMMARY
    FarmerDashboardSummaryResponse getFarmerDashboardSummary();

    // FARMER ORDER SUMMARY
    FarmerOrderSummaryResponse getFarmerOrderSummary();

    // GENERAL ORDER SUMMARY
    GeneralOrderSummaryResponse getGeneralOrderSummary();

    // TRACKING
    void updateTrackingLocation(UUID orderId, TrackingLocationRequest request);
    List<TrackingLocationResponse> getTrackingLocation(UUID orderId);

    // AUTO-CANCEL STALE UNPAID ONLINE ORDERS
    boolean autoCancelStaleOrder(UUID orderId, String reason);
}