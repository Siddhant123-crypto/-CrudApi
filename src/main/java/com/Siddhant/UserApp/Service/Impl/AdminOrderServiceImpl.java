package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.OrderItem;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Service.AdminOrderService;
import com.Siddhant.UserApp.dto.admin.AdminOrderItemResponse;
import com.Siddhant.UserApp.dto.admin.AdminOrderResponse;
import com.Siddhant.UserApp.dto.admin.AdminOrderStatisticsResponse;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public List<AdminOrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdminOrderResponse getOrderById(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        return mapToResponse(order);
    }

    @Override
    public List<AdminOrderResponse> searchOrders(String keyword) {

        List<Order> orders =
                orderRepository
                        .findByOrderNumberContainingIgnoreCaseOrCustomerNameContainingIgnoreCaseOrCustomerMobileContaining(
                                keyword,
                                keyword,
                                keyword
                        );

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AdminOrderResponse> filterOrders(
            OrderStatus status,
            PaymentStatus paymentStatus) {

        List<Order> orders;

        if (status != null && paymentStatus != null) {

            orders = orderRepository.findByStatusAndPaymentStatus(
                    status,
                    paymentStatus
            );

        } else if (status != null) {

            orders = orderRepository.findByStatus(status);

        } else if (paymentStatus != null) {

            orders = orderRepository.findByPaymentStatus(paymentStatus);

        } else {

            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdminOrderResponse updateOrderStatus(
            UUID orderId,
            OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (status == null) {
            throw new RuntimeException(
                    "Order status is required"
            );
        }

        order.setStatus(status);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    @Override
    public AdminOrderResponse updatePaymentStatus(
            UUID orderId,
            PaymentStatus paymentStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (paymentStatus == null) {
            throw new RuntimeException(
                    "Payment status is required"
            );
        }

        order.setPaymentStatus(paymentStatus);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public AdminOrderResponse cancelOrder(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException(
                    "Order is already cancelled"
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException(
                    "Delivered order cannot be cancelled"
            );
        }

        // Restore product quantity
        order.getOrderItems().forEach(item -> {

            Product product = item.getProduct();

            product.setQuantity(
                    product.getQuantity()
                            + item.getQuantity().doubleValue()
            );

            productRepository.save(product);
        });

        order.setStatus(OrderStatus.CANCELLED);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    private AdminOrderResponse mapToResponse(Order order) {

        var customer = order.getCustomer();

        List<AdminOrderItemResponse> items =
                order.getOrderItems()
                        .stream()
                        .map(this::mapItem)
                        .toList();

        return new AdminOrderResponse(
                order.getOrderId(),
                customer.getUserId(),
                customer.getName(),
                customer.getMobile(),
                order.getDeliveryAddress(),
                items,
                order.getTotalAmount(),
                order.getPaymentStatus().name(),
                order.getStatus().name(),
                order.getCreatedOn()
        );
    }

    private AdminOrderItemResponse mapItem(OrderItem item) {

        Product product = item.getProduct();
        FarmerProfile farmer = product.getFarmer();

        return new AdminOrderItemResponse(
                product.getProductId(),
                product.getProductName(),
                product.getProductPhoto(),
                item.getQuantity(),
                product.getUnit().name(),
                item.getPrice(),
                item.getSubtotal(),
                farmer.getId(),
                farmer.getUser().getName()
        );
    }@Override
    public AdminOrderStatisticsResponse getOrderStatistics() {

        long totalOrders = orderRepository.count();

        long pendingOrders =
                orderRepository.countByStatus(OrderStatus.PENDING);

        long confirmedOrders =
                orderRepository.countByStatus(OrderStatus.CONFIRMED);

        long deliveredOrders =
                orderRepository.countByStatus(OrderStatus.DELIVERED);

        long cancelledOrders =
                orderRepository.countByStatus(OrderStatus.CANCELLED);

        long paidOrders =
                orderRepository.countByPaymentStatus(PaymentStatus.PAID);

        long pendingPaymentOrders =
                orderRepository.countByPaymentStatus(PaymentStatus.PENDING);

        var totalOrderAmount =
                orderRepository.getTotalOrderAmount();

        return new AdminOrderStatisticsResponse(
                totalOrders,
                pendingOrders,
                confirmedOrders,
                deliveredOrders,
                cancelledOrders,
                paidOrders,
                pendingPaymentOrders,
                totalOrderAmount
        );
    }@Override
    public List<AdminOrderResponse> getOrdersByFarmer(UUID farmerId) {

        List<Order> orders =
                orderRepository.findDistinctByOrderItemsFarmer_Id(farmerId);

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }
}

