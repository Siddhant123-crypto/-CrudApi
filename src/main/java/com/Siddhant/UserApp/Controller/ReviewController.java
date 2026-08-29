package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.ReviewService;
import com.Siddhant.UserApp.dto.ProductRatingResponse;
import com.Siddhant.UserApp.dto.ReviewRequest;
import com.Siddhant.UserApp.dto.ReviewResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/review")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createReview(
            @RequestPart("data") String data, @RequestPart(value = "photo", required = false) MultipartFile photo, @RequestPart(value = "video", required = false) MultipartFile video
    ) throws IOException {
        ReviewRequest request = objectMapper.readValue(data, ReviewRequest.class);
        log.info("Received request to create review for product: {}", request.getProductId());
        ReviewResponse response = reviewService.createReview(request, photo, video);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/product/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable UUID productId) {
        log.info("Fetching reviews for product: {}", productId);
        return ResponseEntity.ok(
                reviewService.getProductReviews(productId)
        );
    }
    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<ProductRatingResponse> getProductRating(@PathVariable UUID productId) {
        log.info("Fetching rating for product: {}", productId
        );
        return ResponseEntity.ok(reviewService.getProductRating(productId)
        );
    }
    @PutMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable UUID reviewId, @RequestPart("data") String data, @RequestPart(value = "photo", required = false) MultipartFile photo, @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        return null;
    }
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable UUID reviewId) {reviewService.deleteReview(reviewId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Review deleted successfully");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/farmer")
    public ResponseEntity<List<ReviewResponse>> getFarmerReviews(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching reviews for farmer: {}", email
        );
        return ResponseEntity.ok(reviewService.getFarmerReviews()
        );
    }
}