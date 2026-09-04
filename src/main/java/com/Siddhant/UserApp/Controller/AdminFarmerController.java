package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.AdminFarmerService;
import com.Siddhant.UserApp.dto.admin.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/admin/farmers")
@RequiredArgsConstructor
public class AdminFarmerController {
    private final AdminFarmerService adminFarmerService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllFarmers() {
        List<AdminFarmerResponse> farmers = adminFarmerService.getAllFarmers();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farmers fetched successfully");
        response.put("data", farmers);
        return ResponseEntity.ok(response);
    }@GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingFarmers() {
        List<AdminFarmerResponse> farmers = adminFarmerService.getPendingFarmers();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Pending farmers fetched successfully");
        response.put("data", farmers);
        return ResponseEntity.ok(response);
    }@GetMapping("/verified")
    public ResponseEntity<Map<String, Object>> getVerifiedFarmers() {
        List<AdminFarmerResponse> farmers = adminFarmerService.getVerifiedFarmers();Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Verified farmers fetched successfully");
        response.put("data", farmers);
        return ResponseEntity.ok(response);
    }@GetMapping("/blocked")
    public ResponseEntity<Map<String, Object>> getBlockedFarmers() {
        List<AdminFarmerResponse> farmers = adminFarmerService.getBlockedFarmers();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Blocked farmers fetched successfully");
        response.put("data", farmers);
        return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/block")
    public ResponseEntity<Map<String, Object>> blockFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.blockFarmer(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farmer blocked successfully");response.put("data", farmer);return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/unblock")
    public ResponseEntity<Map<String, Object>> unblockFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.unblockFarmer(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Farmer unblocked successfully");
        response.put("data", farmer);return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/verify")
    public ResponseEntity<Map<String, Object>> verifyFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.verifyFarmer(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Farmer verified successfully");
        response.put("data", farmer);return ResponseEntity.ok(response);
    }@DeleteMapping("/{farmerId}")
    public ResponseEntity<Map<String, Object>> deleteFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.deleteFarmer(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Farmer deleted successfully");
        response.put("data", farmer);return ResponseEntity.ok(response);
    }@GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFarmers(@RequestParam String keyword) {
        List<AdminFarmerResponse> farmers = adminFarmerService.searchFarmers(keyword);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", farmers.isEmpty() ? "No farmers found" : "Farmers searched successfully");response.put("data", farmers);
        return ResponseEntity.ok(response);
    }@GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterFarmersByState(@RequestParam String state) {
        List<AdminFarmerResponse> farmers = adminFarmerService.filterFarmersByState(state);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", farmers.isEmpty() ? "No farmers found for this state" : "Farmers filtered successfully");response.put("data", farmers);
        return ResponseEntity.ok(response);
    }@GetMapping("/{farmerId}")
    public ResponseEntity<Map<String, Object>> getFarmerById(
            @PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.getFarmerById(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farmer details fetched successfully");
        response.put("data", farmer);
        return ResponseEntity.ok(response);
    }@GetMapping("/{farmerId}/sales")
    public ResponseEntity<Map<String, Object>> getFarmerSales(@PathVariable UUID farmerId) {
        AdminFarmerSalesResponse sales = adminFarmerService.getFarmerSales(farmerId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farmer sales fetched successfully");response.put("data", sales);
        return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/approve")
    public ResponseEntity<Map<String, Object>> approveFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.approveFarmer(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farmer approved successfully");response.put("data", farmer);
        return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/reject")
    public ResponseEntity<Map<String, Object>> rejectFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.rejectFarmer(farmerId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farmer rejected successfully");response.put("data", farmer);
        return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/unverify")
    public ResponseEntity<Map<String, Object>> unverifyFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.unverifyFarmer(farmerId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farmer unverified successfully");response.put("data", farmer);
        return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/activate")
    public ResponseEntity<Map<String, Object>> activateFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.activateFarmer(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farmer activated successfully");response.put("data", farmer);
        return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateFarmer(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.deactivateFarmer(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Farmer deactivated successfully");response.put("data", farmer);
        return ResponseEntity.ok(response);
    }@GetMapping("/{farmerId}/farm")
    public ResponseEntity<Map<String, Object>> getFarmDetails(@PathVariable UUID farmerId) {
        AdminFarmerResponse farmer = adminFarmerService.getFarmDetails(farmerId);Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Farm details fetched successfully");response.put("data", farmer);
        return ResponseEntity.ok(response);
    }@GetMapping("/{farmerId}/gallery")
    public ResponseEntity<Map<String, Object>> getFarmGallery(@PathVariable UUID farmerId) {
        List<AdminFarmGalleryResponse> gallery = adminFarmerService.getFarmGallery(farmerId);Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Farm gallery fetched successfully");response.put("data", gallery);
        return ResponseEntity.ok(response);
    }@PutMapping("/{farmerId}/request-info")
    public ResponseEntity<Map<String, Object>> requestAdditionalInfo(@PathVariable UUID farmerId,
            @Valid @RequestBody AdminFarmVerificationRequest request) {AdminFarmerResponse farmer = adminFarmerService.requestAdditionalInfo(farmerId, request.getMessage());
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Additional information requested successfully");response.put("data", farmer);
        return ResponseEntity.ok(response);
    }@GetMapping("/{farmerId}/verification")
    public ResponseEntity<Map<String, Object>> getFarmVerification(@PathVariable UUID farmerId) {
        AdminFarmVerificationResponse verification = adminFarmerService.getFarmVerification(farmerId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Farm verification details fetched successfully");response.put("data", verification);
        return ResponseEntity.ok(response);
    }
}