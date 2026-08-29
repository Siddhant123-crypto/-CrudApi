package com.Siddhant.UserApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
public class WishlistResponse {
    private UUID wishlistId;
    private UUID productId;
    private String productName;
    private String category;
    private Double price;
    private Double quantity;
    private String unit;
    private String description;
    private String productPhoto;
    private String productVideo;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
}
