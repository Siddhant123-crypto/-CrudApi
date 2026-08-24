package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.dto.ProductRequest;
import com.Siddhant.UserApp.dto.ProductResponse;
import com.Siddhant.UserApp.dto.ProductUpdateResponse;
import org.springframework.web.multipart.MultipartFile;
import com.Siddhant.UserApp.dto.ProductResponse;
import java.util.List;
import java.util.UUID;
public interface ProductService {
    // Save product with optional image and optional video
    ProductResponse saveProduct(ProductRequest request, MultipartFile photo, MultipartFile video);
    // Get all products
    List<ProductResponse> getAllProducts();
    // Get product by ID
    ProductResponse getProductById(UUID id);
    // Update product with optional new image and video
    ProductUpdateResponse updateProduct(UUID id, ProductRequest request, MultipartFile photo, MultipartFile video);
    // Toggle active/inactive status
    ProductResponse toggleProductStatus(UUID id);
    // Delete product
    String deleteProduct(UUID id);
    // Search by product name
    List<ProductResponse> searchProducts(String keyword);
    // Filter by price
    List<ProductResponse> findByPriceBetween(Double minPrice, Double maxPrice);
    List<ProductResponse> filterByPrice(Double minPrice, Double maxPrice);
    List<ProductResponse> filterByCategoryAndPrice(String category, Double minPrice, Double maxPrice
    );
    // Get products by farmer
    List<Product> getProductsByFarmer(UUID farmerId);
    // Get active products
    List<ProductResponse> getActiveProducts();
    // Upload/update product photo separately
    String uploadPhoto(UUID productId, MultipartFile photo);
    // Upload/update product video separately
    String uploadVideo(UUID productId, MultipartFile video);
}