package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.WishlistService;
import com.Siddhant.UserApp.dto.WishlistCheckResponse;
import com.Siddhant.UserApp.dto.WishlistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToWishlist(@PathVariable UUID productId) {
        WishlistResponse wishlist = wishlistService.addToWishlist(productId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Product added to wishlist successfully");
        response.put("wishlist", wishlist);
        return ResponseEntity.ok(response);
    }@GetMapping("/my")
    public ResponseEntity<?> getMyWishlist() {
        List<WishlistResponse> wishlist = wishlistService.getMyWishlist();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Wishlist fetched successfully");
        response.put("wishlist", wishlist);
        return ResponseEntity.ok(response);
    }@DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable UUID productId) {
        wishlistService.removeFromWishlist(productId);
        return ResponseEntity.ok(Map.of(
                "message", "Product removed from wishlist successfully"
        ));
    }@GetMapping("/check/{productId}")
    public ResponseEntity<?> checkWishlist(@PathVariable UUID productId) {
        WishlistCheckResponse wishlist = wishlistService.checkWishlist(productId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Wishlist status fetched successfully");
        response.put("wishlist", wishlist);
        return ResponseEntity.ok(response);
    }
}