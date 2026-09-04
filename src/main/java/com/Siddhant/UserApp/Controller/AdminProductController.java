package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.AdminProductService;
import com.Siddhant.UserApp.dto.admin.AdminProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final AdminProductService adminProductService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        List<AdminProductResponse> products = adminProductService.getAllProducts();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Products fetched successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }@GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable UUID productId) {
        AdminProductResponse product = adminProductService.getProductById(productId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Product details fetched successfully");response.put("data", product);
        return ResponseEntity.ok(response);
    }@GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveProducts() {
        List<AdminProductResponse> products = adminProductService.getActiveProducts();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Active products fetched successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }@GetMapping("/inactive")
    public ResponseEntity<Map<String, Object>> getInactiveProducts() {
        List<AdminProductResponse> products = adminProductService.getInactiveProducts();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Inactive products fetched successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }@PutMapping("/{productId}/block")
    public ResponseEntity<Map<String, Object>> blockProduct(@PathVariable UUID productId) {
        AdminProductResponse product = adminProductService.blockProduct(productId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Product blocked successfully");response.put("data", product);
        return ResponseEntity.ok(response);
    }@PutMapping("/{productId}/unblock")
    public ResponseEntity<Map<String, Object>> unblockProduct(@PathVariable UUID productId) {
        AdminProductResponse product = adminProductService.unblockProduct(productId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Product unblocked successfully");
        response.put("data", product);
        return ResponseEntity.ok(response);
    }@DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable UUID productId) {
        AdminProductResponse product = adminProductService.deleteProduct(productId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Product deleted successfully");response.put("data", product);
        return ResponseEntity.ok(response);
    }@GetMapping("/farmer/{farmerId}")
    public ResponseEntity<Map<String, Object>> getProductsByFarmer(@PathVariable UUID farmerId) {
        List<AdminProductResponse> products = adminProductService.getProductsByFarmer(farmerId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", products.isEmpty() ? "No products found for this farmer" : "Farmer products fetched successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }@GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String keyword) {
        List<AdminProductResponse> products = adminProductService.searchProducts(keyword);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Products search completed successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }@GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterProducts(@RequestParam(required = false) String category, @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice) {
        List<AdminProductResponse> products = adminProductService.filterProducts(category, minPrice, maxPrice);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Products filtered successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }@PutMapping("/{productId}/approve")
    public ResponseEntity<Map<String, Object>> approveProduct(@PathVariable UUID productId) {
        AdminProductResponse product = adminProductService.approveProduct(productId);Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Product approved successfully");
        response.put("data", product);
        return ResponseEntity.ok(response);
    }@PutMapping("/{productId}/reject")
    public ResponseEntity<Map<String, Object>> rejectProduct(@PathVariable UUID productId) {
        AdminProductResponse product = adminProductService.rejectProduct(productId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Product rejected successfully");response.put("data", product);
        return ResponseEntity.ok(response);
    }@PutMapping("/{productId}/activate")
    public ResponseEntity<Map<String, Object>> activateProduct(@PathVariable UUID productId) {
        AdminProductResponse product = adminProductService.activateProduct(productId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Product activated successfully");response.put("data", product);
        return ResponseEntity.ok(response);
    }@PutMapping("/{productId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateProduct(@PathVariable UUID productId) {
        AdminProductResponse product = adminProductService.deactivateProduct(productId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Product deactivated successfully");response.put("data", product);
        return ResponseEntity.ok(response);
    }@GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingProducts() {
        List<AdminProductResponse> products = adminProductService.getPendingProducts();
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Pending products fetched successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }@GetMapping("/approved")
    public ResponseEntity<Map<String, Object>> getApprovedProducts() {
        List<AdminProductResponse> products = adminProductService.getApprovedProducts();
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Approved products fetched successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }@GetMapping("/rejected")
    public ResponseEntity<Map<String, Object>> getRejectedProducts() {
        List<AdminProductResponse> products = adminProductService.getRejectedProducts();
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Rejected products fetched successfully");response.put("data", products);
        return ResponseEntity.ok(response);
    }
}