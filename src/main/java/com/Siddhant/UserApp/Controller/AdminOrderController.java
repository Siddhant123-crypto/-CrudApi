package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.AdminOrderService;
import com.Siddhant.UserApp.dto.admin.*;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final AdminOrderService adminOrderService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        List<AdminOrderResponse> orders =
                adminOrderService.getAllOrders();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Orders fetched successfully");
        response.put("data", orders);
        return ResponseEntity.ok(response);
    }@GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable UUID orderId) {
        AdminOrderResponse order = adminOrderService.getOrderById(orderId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Order details fetched successfully");response.put("data", order);
        return ResponseEntity.ok(response);
    }@GetMapping("/search")
    public ResponseEntity<?> searchOrders(@RequestParam String keyword) {
        return ResponseEntity.ok(Map.of("message", "Orders searched successfully", "data", adminOrderService.searchOrders(keyword)));
    }@GetMapping("/filter")
    public ResponseEntity<?> filterOrders(@RequestParam(required = false) OrderStatus status, @RequestParam(required = false) PaymentStatus paymentStatus) {
        List<AdminOrderResponse> orders = adminOrderService.filterOrders(status, paymentStatus);
        Map<String, Object> response = new LinkedHashMap<>();
        if (orders.isEmpty()) {response.put("message", "No orders found for the given filter");
        } else {response.put("message", "Orders filtered successfully");response.put("data", orders);
        }return ResponseEntity.ok(response);
    }@PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable UUID orderId, @RequestBody AdminOrderStatusRequest request) {
        AdminOrderResponse response = adminOrderService.updateOrderStatus(orderId, request.getStatus());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Order status updated successfully");result.put("data", response);return ResponseEntity.ok(result);
    }@PutMapping("/{orderId}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable UUID orderId, @RequestBody AdminPaymentStatusRequest request) {
        AdminOrderResponse response = adminOrderService.updatePaymentStatus(orderId, request.getPaymentStatus());Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Payment status updated successfully");result.put("data", response);
        return ResponseEntity.ok(result);
    }@PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID orderId) {
        AdminOrderResponse response = adminOrderService.cancelOrder(orderId);
        Map<String, Object> result = new LinkedHashMap<>();result.put("message", "Order cancelled successfully");
        result.put("data", response);
        return ResponseEntity.ok(result);
    }@GetMapping("/statistics")
    public ResponseEntity<?> getOrderStatistics() {
        AdminOrderStatisticsResponse statistics = adminOrderService.getOrderStatistics();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Order statistics fetched successfully");response.put("data", statistics);
        return ResponseEntity.ok(response);
    }@GetMapping("/farmer/{farmerId}")
    public ResponseEntity<Map<String, Object>> getOrdersByFarmer(@PathVariable UUID farmerId) {
        List<AdminOrderResponse> orders = adminOrderService.getOrdersByFarmer(farmerId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", orders.isEmpty() ? "No orders found for this farmer" : "Farmer orders fetched successfully");response.put("data", orders);
        return ResponseEntity.ok(response);
    }
}