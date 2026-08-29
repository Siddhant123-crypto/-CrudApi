package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.*;
import com.Siddhant.UserApp.Repository.*;
import com.Siddhant.UserApp.Service.FileStorageService;
import com.Siddhant.UserApp.Service.ReviewService;
import com.Siddhant.UserApp.dto.ProductRatingResponse;
import com.Siddhant.UserApp.dto.ReviewMediaResponse;
import com.Siddhant.UserApp.dto.ReviewRequest;
import com.Siddhant.UserApp.dto.ReviewResponse;
import com.Siddhant.UserApp.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewRepository reviewRepository;
    private final ReviewMediaRepository reviewMediaRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final FileStorageService fileStorageService;
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated"
            );
        }return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }private FarmerProfile getLoggedInFarmer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Farmer is not authenticated");
        }return farmerProfileRepository.findByUser_Email(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Farmer profile not found"));
    }private boolean isLoggedInFarmer() {Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {return false;
        }return authentication.getAuthorities().stream().anyMatch(authority -> {String role = authority.getAuthority();return role.equals("FARMER") || role.equals("ROLE_FARMER");});
    }@Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, MultipartFile photo, MultipartFile video) throws IOException {log.info("createReview request: {}", request);
        try {User customer = getLoggedInUser();Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            if (order.getCustomer() == null || !order.getCustomer().getUserId().equals(customer.getUserId())) {throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to review this order");
            }
            // Temporarily/Permanently bypassing the delivered order check
            // if (order.getStatus() != OrderStatus.DELIVERED) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only review products from delivered orders");
            // }
            boolean productInOrder = order.getOrderItems().stream().anyMatch(item -> item.getProduct() != null && item.getProduct().getProductId().equals(request.getProductId()));
            if (!productInOrder) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This product is not part of the specified order"
                );
            }Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));Optional<Review> existingReview = reviewRepository.findByCustomerUserIdAndProductProductIdAndOrderOrderId(customer.getUserId(), product.getProductId(), order.getOrderId());
            if (existingReview.isPresent()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already reviewed this product for this order");
            }if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating is required and must be between 1 and 5");
            }Review review = new Review();review.setCustomer(customer);review.setProduct(product);review.setOrder(order);review.setRating(request.getRating());review.setReviewText(request.getReviewText());review.setIsVerifiedPurchase(true);
            Review savedReview = reviewRepository.save(review);List<ReviewMedia> mediaList = new ArrayList<>();
            if (photo != null && !photo.isEmpty()) {String contentType = photo.getContentType();
                if (contentType == null || (!contentType.toLowerCase().startsWith("image/") && !contentType.equals("application/octet-stream"))) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image format: " + contentType);
                }String fileName = fileStorageService.saveFile(photo);ReviewMedia imageMedia = new ReviewMedia();imageMedia.setReview(savedReview);imageMedia.setMediaType("IMAGE");imageMedia.setMediaUrl(fileName);reviewMediaRepository.save(imageMedia);mediaList.add(imageMedia);
            }if (video != null && !video.isEmpty()) {String contentType = video.getContentType();
                if (contentType == null || (!contentType.toLowerCase().startsWith("video/") && !contentType.equals("application/octet-stream"))) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid video format: " + contentType);
                }String fileName = fileStorageService.saveFile(video);ReviewMedia videoMedia = new ReviewMedia();videoMedia.setReview(savedReview);videoMedia.setMediaType("VIDEO");videoMedia.setMediaUrl(fileName);reviewMediaRepository.save(videoMedia);mediaList.add(videoMedia);
            }savedReview.setMedia(mediaList);recalculateProductRating(product.getProductId());return convertToResponse(savedReview);
        } catch (Exception e) {log.error("Exception in createReview: ", e);
            if (e instanceof ResponseStatusException) {throw (ResponseStatusException) e;
            }throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }@Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(UUID productId) {
        log.info("Getting reviews for product: {}", productId);Product product = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (isLoggedInFarmer()) {log.info("Logged in user is FARMER. Checking product ownership.");FarmerProfile farmer = getLoggedInFarmer();List<Product> farmerProducts = productRepository.findByFarmer(farmer);boolean isOwnProduct = farmerProducts.stream().anyMatch(farmerProduct -> farmerProduct.getProductId().equals(productId));
            if (!isOwnProduct) {log.warn("Farmer tried to access another farmer's product reviews. productId={}", productId);throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view reviews of your own products");
            }
        }List<Review> reviews = reviewRepository.findByProductProductId(productId);return reviews.stream().map(this::convertToResponse).collect(Collectors.toList());
    }@Override
    @Transactional(readOnly = true)
    public ProductRatingResponse getProductRating(UUID productId) {Product product = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));ProductRatingResponse response = new ProductRatingResponse();response.setProductId(product.getProductId());response.setProductName(product.getProductName());response.setAverageRating(product.getAverageRating());response.setReviewCount(product.getReviewCount());
        return response;
    }@Override
    @Transactional
    public ReviewResponse updateReview(UUID reviewId, ReviewRequest request, MultipartFile photo, MultipartFile video
    ) throws IOException {
        log.info("updateReview request reviewId={}, {}", reviewId, request);try {User customer = getLoggedInUser();Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
            if (!review.getCustomer().getUserId().equals(customer.getUserId())) {throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this review");
            }if (request.getRating() != null) {if (request.getRating() < 1 || request.getRating() > 5) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
                }review.setRating(request.getRating());
            }if (request.getReviewText() != null) {review.setReviewText(request.getReviewText());
            }if (photo != null && !photo.isEmpty()) {String contentType = photo.getContentType();
                if (contentType == null || (!contentType.toLowerCase().startsWith("image/") && !contentType.equals("application/octet-stream"))) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image format: " + contentType);
                }String fileName = fileStorageService.saveFile(photo);ReviewMedia imageMedia = new ReviewMedia();imageMedia.setReview(review);imageMedia.setMediaType("IMAGE");imageMedia.setMediaUrl(fileName);reviewMediaRepository.save(imageMedia);review.getMedia().add(imageMedia);
            }if (video != null && !video.isEmpty()) {String contentType = video.getContentType();if (contentType == null || (!contentType.toLowerCase().startsWith("video/") && !contentType.equals("application/octet-stream"))) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid video format: " + contentType);
                }String fileName = fileStorageService.saveFile(video);ReviewMedia videoMedia = new ReviewMedia();videoMedia.setReview(review);videoMedia.setMediaType("VIDEO");videoMedia.setMediaUrl(fileName);reviewMediaRepository.save(videoMedia);review.getMedia().add(videoMedia);
            }Review updatedReview = reviewRepository.save(review);recalculateProductRating(review.getProduct().getProductId());return convertToResponse(updatedReview);
        } catch (Exception e) {log.error("Exception in updateReview: ", e);
            if (e instanceof ResponseStatusException) {throw (ResponseStatusException) e;
            }throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }@Override
    @Transactional
    public void deleteReview(UUID reviewId) {User customer = getLoggedInUser();
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!review.getCustomer().getUserId().equals(customer.getUserId())) {throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this review");
        }UUID productId = review.getProduct().getProductId();reviewRepository.delete(review);recalculateProductRating(productId);
    }@Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getFarmerReviews() {FarmerProfile farmer = getLoggedInFarmer();List<Product> farmerProducts = productRepository.findByFarmer(farmer);List<ReviewResponse> farmerReviews = new ArrayList<>();
        for (Product product : farmerProducts) {List<Review> reviews = reviewRepository.findByProductProductId(product.getProductId());for (Review review : reviews) {farmerReviews.add(convertToResponse(review));
            }
        }return farmerReviews;
    }private void recalculateProductRating(UUID productId) {Product product = productRepository.findById(productId).orElse(null);if (product == null) {return;
        }List<Review> reviews = reviewRepository.findByProductProductId(productId);int count = reviews.size();double sum = 0.0;for (Review review : reviews) {sum += review.getRating();
        }double average = count > 0 ? sum / count : 0.0;average = Math.round(average * 10.0) / 10.0;product.setReviewCount(count);product.setAverageRating(average);product.setUpdatedOn(LocalDateTime.now());productRepository.save(product);
    }private ReviewResponse convertToResponse(Review review) {ReviewResponse response = new ReviewResponse();response.setReviewId(review.getReviewId());response.setProductId(review.getProduct().getProductId());response.setOrderId(review.getOrder().getOrderId());response.setProductName(review.getProduct().getProductName());response.setRating(review.getRating());response.setReviewText(review.getReviewText());response.setIsVerifiedPurchase(review.getIsVerifiedPurchase());response.setCreatedAt(review.getCreatedAt());response.setUpdatedAt(review.getUpdatedAt());String displayName = "Anonymous";
        if (review.getCustomer() != null) {String fullName = review.getCustomer().getName();
            if (fullName != null && !fullName.trim().isEmpty()) {String[] parts = fullName.trim().split("\\s+");
                if (parts.length > 1) {displayName = parts[0] + " " + parts[parts.length - 1].substring(0, 1) + ".";
                } else {displayName = parts[0];
                }
            }
        }response.setCustomerDisplayName(displayName);
        if (review.getMedia() != null) {List<ReviewMediaResponse> mediaResponses = review.getMedia().stream().map(m -> {ReviewMediaResponse rmr = new ReviewMediaResponse();rmr.setMediaId(m.getMediaId());rmr.setMediaType(m.getMediaType());rmr.setMediaUrl(m.getMediaUrl());rmr.setCreatedAt(m.getCreatedAt());return rmr;}).collect(Collectors.toList());response.setMedia(mediaResponses);}
        return response;
    }
}