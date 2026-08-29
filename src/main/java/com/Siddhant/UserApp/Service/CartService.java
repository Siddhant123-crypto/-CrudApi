package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.CartQuantityRequest;
import com.Siddhant.UserApp.dto.CartResponse;
import java.util.UUID;
public interface CartService {
    CartResponse addToCart(UUID productId, CartQuantityRequest request);
    CartResponse getMyCart();
    CartResponse updateQuantity(UUID productId, CartQuantityRequest request);
    void removeFromCart(UUID productId);
    void clearCart();
}
