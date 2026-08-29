package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
public class ReviewResponse {
    private UUID reviewId;
    private UUID productId;
    private UUID orderId;
    private String productName;
    private String customerDisplayName;
    private Integer rating;
    private String reviewText;
    private Boolean isVerifiedPurchase;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReviewMediaResponse> media;
}
