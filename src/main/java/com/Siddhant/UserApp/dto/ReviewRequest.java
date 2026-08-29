package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@NoArgsConstructor
public class ReviewRequest {
    private UUID productId;
    private UUID orderId;
    private Integer rating;
    private String reviewText;
}
