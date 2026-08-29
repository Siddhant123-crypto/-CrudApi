package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.CartService;
import com.Siddhant.UserApp.dto.CartQuantityRequest;
import com.Siddhant.UserApp.dto.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToCart(@PathVariable UUID productId,@RequestBody CartQuantityRequest request) {
        CartResponse cart = cartService.addToCart(productId,request);
        return ResponseEntity.ok(Map.of(
                "message","Product added to cart successfully",
                "cart",cart
        ));
    }@GetMapping("/my")
    public ResponseEntity<?> getMyCart() {return ResponseEntity.ok(Map.of("message","Cart fetched successfully","cart",cartService.getMyCart()));
    }@PutMapping("/update/{productId}")
    public ResponseEntity<?> updateQuantity(@PathVariable UUID productId,@RequestBody CartQuantityRequest request) {return ResponseEntity.ok(Map.of("message","Cart quantity updated successfully","cart",cartService.updateQuantity(productId,request)));
    }@DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable UUID productId) {cartService.removeFromCart(productId);return ResponseEntity.ok(Map.of("message","Product removed from cart successfully"));
    }@DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {cartService.clearCart();return ResponseEntity.ok(Map.of("message","Cart cleared successfully"));
    }
}


