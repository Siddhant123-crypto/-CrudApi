package com.Siddhant.UserApp.dto.admin;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminCustomerOrderResponse {
    private UUID orderId;
    private String orderNumber;
    private UUID customerId;
    private String customerName;
    private String customerMobile;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime orderDate;
}