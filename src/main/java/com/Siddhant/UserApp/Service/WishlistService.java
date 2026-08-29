package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.WishlistCheckResponse;
import com.Siddhant.UserApp.dto.WishlistResponse;
import java.util.List;
import java.util.UUID;
public interface WishlistService {
    WishlistResponse addToWishlist(UUID productId);
    List<WishlistResponse> getMyWishlist();
    void removeFromWishlist(UUID productId);
    WishlistCheckResponse checkWishlist(UUID productId);
}
