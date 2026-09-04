package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.admin.AdminProductResponse;
import java.util.List;
import java.util.UUID;
public interface AdminProductService {
    List<AdminProductResponse> getAllProducts();
    AdminProductResponse getProductById(UUID productId);
    List<AdminProductResponse> getActiveProducts();
    List<AdminProductResponse> getInactiveProducts();
    AdminProductResponse blockProduct(UUID productId);
    AdminProductResponse unblockProduct(UUID productId);
    AdminProductResponse deleteProduct(UUID productId);
    List<AdminProductResponse> getProductsByFarmer(UUID farmerId);
    List<AdminProductResponse> searchProducts(String keyword);
    List<AdminProductResponse> filterProducts(String category, Double minPrice, Double maxPrice);
    AdminProductResponse approveProduct(UUID productId);
    AdminProductResponse rejectProduct(UUID productId);
    AdminProductResponse activateProduct(UUID productId);
    AdminProductResponse deactivateProduct(UUID productId);
    List<AdminProductResponse> getPendingProducts();
    List<AdminProductResponse> getApprovedProducts();
    List<AdminProductResponse> getRejectedProducts();
}