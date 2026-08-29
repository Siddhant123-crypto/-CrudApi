package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.ProductRatingResponse;
import com.Siddhant.UserApp.dto.ReviewRequest;
import com.Siddhant.UserApp.dto.ReviewResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, MultipartFile photo, MultipartFile video
    ) throws IOException;
    List<ReviewResponse> getProductReviews(UUID productId);
    ProductRatingResponse getProductRating(UUID productId);
    ReviewResponse updateReview(UUID reviewId, ReviewRequest request, MultipartFile photo, MultipartFile video
    ) throws IOException;
    void deleteReview(UUID reviewId);
    List<ReviewResponse> getFarmerReviews();
}