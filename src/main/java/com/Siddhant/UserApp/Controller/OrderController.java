package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.OrderService;
import com.Siddhant.UserApp.dto.*;
import com.Siddhant.UserApp.enums.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {this.orderService = orderService;}
    @PostMapping("/create")
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody OrderRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request);
        OrderCreateResponse response = new OrderCreateResponse("Order created successfully", orderResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/customer/my-orders")
    public ResponseEntity<List<OrderResponse>> getCustomerMyOrders() {
        List<OrderResponse> response = orderService.getCustomerMyOrders();return ResponseEntity.ok(response);
    }
    @GetMapping("/customer/order-history")
    public ResponseEntity<List<OrderResponse>> getCustomerOrderHistory() {
        List<OrderResponse> response = orderService.getCustomerOrderHistory();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/farmer")
    public ResponseEntity<List<OrderResponse>> getFarmerOrders() {
        List<OrderResponse> response = orderService.getFarmerOrders();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/farmer/status/{status}")
    public ResponseEntity<List<OrderResponse>>
    getFarmerOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> response = orderService.getFarmerOrdersByStatus(status);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable UUID orderId, @RequestParam OrderStatus status) {
        OrderResponse response = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam(required = false) String reason) {
        OrderResponse response = orderService.cancelOrder(orderId, reason);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/cancel-reason")
    public ResponseEntity<OrderResponse> updateCancellationReason(
            @PathVariable UUID orderId,
            @RequestParam String reason) {
        OrderResponse response = orderService.updateCancellationReason(orderId, reason);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{orderId}/farmer-cancel")
    public ResponseEntity<FarmerCancelOrderResponse>
    farmerCancelOrder(@PathVariable UUID orderId) {
        FarmerCancelOrderResponse response = orderService.farmerCancelOrder(orderId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{orderId}/payment")
    public ResponseEntity<PaymentResponse> updatePayment(@PathVariable UUID orderId, @RequestBody PaymentRequest request) {
        OrderResponse response = orderService.updatePayment(orderId, request);
        PaymentResponse paymentResponse = new PaymentResponse("Payment updated successfully", response);
        return ResponseEntity.ok(paymentResponse);
    }
    @PutMapping("/{orderId}/accept")
    public ResponseEntity<OrderResponse> acceptOrder(@PathVariable UUID orderId) {
        OrderResponse response = orderService.acceptOrder(orderId);
        return ResponseEntity.ok(response);
    }
}