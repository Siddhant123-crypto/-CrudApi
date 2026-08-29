package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Entity.Wishlist;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Repository.WishlistRepository;
import com.Siddhant.UserApp.Service.WishlistService;
import com.Siddhant.UserApp.dto.WishlistCheckResponse;
import com.Siddhant.UserApp.dto.WishlistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private User getLoggedInUser() {Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }@Override
    @Transactional
    public WishlistResponse addToWishlist(UUID productId) {
        User customer = getLoggedInUser();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (Boolean.FALSE.equals(product.getIsActive()) || Boolean.TRUE.equals(product.getIsDelete())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This product is not available");
        }boolean alreadyExists = wishlistRepository.existsByCustomerUserIdAndProductProductId(customer.getUserId(), productId);
        if (alreadyExists) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is already in your wishlist");
        }Wishlist wishlist = new Wishlist();wishlist.setCustomer(customer);wishlist.setProduct(product);
        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return convertToResponse(savedWishlist);
    }@Override
    @Transactional(readOnly = true) public List<WishlistResponse> getMyWishlist() {
        User customer = getLoggedInUser();
        List<Wishlist> wishlistList = wishlistRepository.findByCustomerUserId(customer.getUserId());
        return wishlistList.stream().map(this::convertToResponse).collect(Collectors.toList());
    }@Override
    @Transactional
    public void removeFromWishlist(UUID productId) {User customer = getLoggedInUser();
        Wishlist wishlist = wishlistRepository.findByCustomerUserIdAndProductProductId(customer.getUserId(), productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not in your wishlist"));
        wishlistRepository.delete(wishlist);
    }@Override
    @Transactional(readOnly = true)
    public WishlistCheckResponse checkWishlist(UUID productId) {
        User customer = getLoggedInUser();
        if (!productRepository.existsById(productId)) {throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }boolean inWishlist = wishlistRepository.existsByCustomerUserIdAndProductProductId(customer.getUserId(), productId);WishlistCheckResponse response = new WishlistCheckResponse();response.setProductId(productId);response.setInWishlist(inWishlist);
        return response;
    }private WishlistResponse convertToResponse(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        WishlistResponse response = new WishlistResponse();response.setWishlistId(wishlist.getWishlistId());response.setProductId(product.getProductId());response.setProductName(product.getProductName());response.setCategory(product.getCategory());response.setPrice(product.getPrice());response.setQuantity(product.getQuantity());
        if (product.getUnit() != null) {response.setUnit(product.getUnit().toString());
        }response.setDescription(product.getDescription());response.setProductPhoto(product.getProductPhoto()
        );response.setProductVideo(product.getProductVideo());response.setAverageRating(product.getAverageRating());response.setReviewCount(product.getReviewCount());response.setCreatedAt(wishlist.getCreatedAt());
        return response;
    }
}