package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@NoArgsConstructor
public class ProductRatingResponse {
    private UUID productId;
    private String productName;
    private Double averageRating;
    private Integer reviewCount;
}