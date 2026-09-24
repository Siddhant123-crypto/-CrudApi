package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.dto.market.MarketRequest;
import com.Siddhant.UserApp.dto.market.MarketResponse;
import com.Siddhant.UserApp.Service.MarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/admin/markets")
public class AdminMarketController {
    private final MarketService marketService;
    public AdminMarketController(MarketService marketService) {this.marketService = marketService;
    }@PostMapping
    public ResponseEntity<?> createMarket(@RequestBody MarketRequest request) {
        MarketResponse market = marketService.createMarket(request);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Market created successfully");
        response.put("data", market);
        return ResponseEntity.ok(response);
    }@PutMapping("/{id}")
    public ResponseEntity<?> updateMarket(@PathVariable UUID id, @RequestBody MarketRequest request) {
        MarketResponse market = marketService.updateMarket(id, request);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Market updated successfully");
        response.put("data", market);
        return ResponseEntity.ok(response);
    }@PutMapping("/{id}/status")
    public ResponseEntity<?> updateMarketStatus(@PathVariable UUID id, @RequestParam Boolean isActive) {
        MarketResponse market = marketService.updateMarketStatus(id, isActive);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(
                "message",
                isActive
                        ? "Market activated successfully"
                        : "Market deactivated successfully"
        );response.put("data", market);
        return ResponseEntity.ok(response);
    }@DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMarket(@PathVariable UUID id) {marketService.deleteMarket(id);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Market deleted successfully");
        response.put("data", null);
        return ResponseEntity.ok(response);
    }
}