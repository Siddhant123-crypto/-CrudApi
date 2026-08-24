package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.enums.PaymentMethod;
import lombok.Data;
import java.util.List;
@Data
public class OrderRequest {
    private PaymentMethod paymentMethod;
    private String deliveryAddress;
    private String village;
    private String postalCode;
    private String state;
    private List<OrderItemRequest> items;
}