package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.dto.market.MarketResponse;
import com.Siddhant.UserApp.dto.market.NearbyMarketResponse;
import com.Siddhant.UserApp.Service.MarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/markets")
public class MarketController {
    private final MarketService marketService;
    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }@GetMapping
    public ResponseEntity<?> getAllMarkets() {
        List<MarketResponse> markets = marketService.getAllMarkets();
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Markets fetched successfully");response.put("data", markets);
        return ResponseEntity.ok(response);
    }@GetMapping("/{id}")
    public ResponseEntity<?> getMarketById(@PathVariable UUID id) {
        MarketResponse market = marketService.getMarketById(id);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Market fetched successfully");response.put("data", market);
        return ResponseEntity.ok(response);
    }@GetMapping("/nearby")
    public ResponseEntity<?> getNearbyMarkets(@RequestParam Double latitude,
            @RequestParam Double longitude) {
        List<NearbyMarketResponse> markets = marketService.getNearbyMarkets(latitude, longitude);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Nearby markets fetched successfully");response.put("data", markets);
        return ResponseEntity.ok(response);
    }@GetMapping("/search")
    public ResponseEntity<?> searchMarkets(@RequestParam String name) {
        List<MarketResponse> markets = marketService.searchMarketsByName(name);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Markets searched successfully");response.put("data", markets);
        return ResponseEntity.ok(response);
    }
}