package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Service.AdminCustomerService;
import com.Siddhant.UserApp.dto.admin.AdminCustomerOrderResponse;
import com.Siddhant.UserApp.dto.admin.AdminCustomerResponse;
import com.Siddhant.UserApp.dto.admin.AdminCustomerStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {
    private final AdminCustomerService adminCustomerService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCustomers() {List<AdminCustomerResponse> customers = adminCustomerService.getAllCustomers();Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Customers fetched successfully");response.put("data", customers);
        return ResponseEntity.ok(response);
    }@GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveCustomers() {List<AdminCustomerResponse> customers = adminCustomerService.getActiveCustomers();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Active customers fetched successfully");response.put("data", customers);
        return ResponseEntity.ok(response);
    }@GetMapping("/blocked")
    public ResponseEntity<Map<String, Object>> getBlockedCustomers() {List<AdminCustomerResponse> customers = adminCustomerService.getBlockedCustomers();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Blocked customers fetched successfully");response.put("data", customers);
        return ResponseEntity.ok(response);
    }@PutMapping("/{customerId}/block")
    public ResponseEntity<Map<String, Object>> blockCustomer(@PathVariable UUID customerId) {AdminCustomerResponse customer = adminCustomerService.blockCustomer(customerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Customer blocked successfully");
        response.put("data", customer);
        return ResponseEntity.ok(response);
    }@PutMapping("/{customerId}/unblock")
    public ResponseEntity<Map<String, Object>> unblockCustomer(@PathVariable UUID customerId) {AdminCustomerResponse customer = adminCustomerService.unblockCustomer(customerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Customer unblocked successfully");
        response.put("data", customer);
        return ResponseEntity.ok(response);
    }@DeleteMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable UUID customerId) {AdminCustomerResponse customer = adminCustomerService.deleteCustomer(customerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Customer deleted successfully");
        response.put("data", customer);
        return ResponseEntity.ok(response);
    }@GetMapping("/{customerId}/orders")
    public ResponseEntity<Map<String, Object>> getCustomerOrderHistory(@PathVariable UUID customerId) {List<AdminCustomerOrderResponse> orders = adminCustomerService.getCustomerOrderHistory(customerId);Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Customer order history fetched successfully");response.put("data", orders);return ResponseEntity.ok(response);
    }@GetMapping("/{customerId}/statistics")
    public ResponseEntity<Map<String, Object>> getCustomerStatistics(@PathVariable UUID customerId) {
        AdminCustomerStatisticsResponse statistics = adminCustomerService.getCustomerStatistics(customerId);Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Customer statistics fetched successfully");response.put("data", statistics);return ResponseEntity.ok(response);
    }@GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCustomers(@RequestParam String keyword) {List<AdminCustomerResponse> customers = adminCustomerService.searchCustomers(keyword);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Customers search completed successfully");response.put("data", customers);
        return ResponseEntity.ok(response);
    }@GetMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable UUID customerId) {
        AdminCustomerResponse customer = adminCustomerService.getCustomerById(customerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Customer details fetched successfully");
        response.put("data", customer);
        return ResponseEntity.ok(response);
    }@GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterCustomers(@RequestParam(required = false) String state, @RequestParam(required = false) Status status) {
        List<AdminCustomerResponse> customers = adminCustomerService.filterCustomers(state, status);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Customers filtered successfully");
        response.put("data", customers);
        return ResponseEntity.ok(response);
    }@PutMapping("/{customerId}/activate")
    public ResponseEntity<Map<String, Object>> activateCustomer(@PathVariable UUID customerId) {
        AdminCustomerResponse customer = adminCustomerService.activateCustomer(customerId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Customer activated successfully");response.put("data", customer);
        return ResponseEntity.ok(response);
    }@PutMapping("/{customerId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateCustomer(@PathVariable UUID customerId) {
        AdminCustomerResponse customer = adminCustomerService.deactivateCustomer(customerId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Customer deactivated successfully");response.put("data", customer);
        return ResponseEntity.ok(response);
    }
}