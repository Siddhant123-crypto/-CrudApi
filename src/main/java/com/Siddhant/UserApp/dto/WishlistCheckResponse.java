package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@NoArgsConstructor
public class WishlistCheckResponse {
    private UUID productId;
    private Boolean inWishlist;
}
