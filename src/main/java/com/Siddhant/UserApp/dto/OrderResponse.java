package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentMethod;
import com.Siddhant.UserApp.enums.PaymentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
public class OrderResponse {
    private UUID orderId;
    private String orderNumber;
    private UUID customerId;
    private UUID farmerId;
    private String customerName;
    private String customerMobile;
    private String deliveryAddress;
    private String village;
    private String postalCode;
    private String state;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String cancellationReason;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private List<OrderItemResponse> items;
}