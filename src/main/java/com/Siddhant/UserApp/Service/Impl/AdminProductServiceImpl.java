package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Service.AdminProductService;
import com.Siddhant.UserApp.dto.admin.AdminProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {
    private final ProductRepository productRepository;
    @Override
    public List<AdminProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToResponse).toList();
    }@Override
    public AdminProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }@Override
    public List<AdminProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrueAndIsDeleteFalse().stream().map(this::mapToResponse).toList();
    }@Override
    public List<AdminProductResponse> getInactiveProducts() {
        return productRepository.findAll().stream().filter(product -> Boolean.FALSE.equals(product.getIsActive()) && !Boolean.TRUE.equals(product.getIsDelete())).map(this::mapToResponse).toList();
    }@Override
    public AdminProductResponse blockProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsActive(false);product.setStatus("BLOCKED");
        productRepository.save(product);return mapToResponse(product);
    }@Override
    public AdminProductResponse unblockProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsActive(true);product.setStatus("ACTIVE");
        productRepository.save(product);return mapToResponse(product);
    }@Override
    public AdminProductResponse deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsDelete(true);product.setIsActive(false);product.setStatus("DELETED");
        productRepository.save(product);return mapToResponse(product);
    }private AdminProductResponse mapToResponse(Product product) {return new AdminProductResponse(product.getProductId(), product.getFarmer() != null ? product.getFarmer().getId() : null,
                product.getFarmer() != null && product.getFarmer().getUser() != null ? product.getFarmer().getUser().getName() : null, product.getProductName(), product.getCategory(), product.getPrice(), product.getQuantity(), product.getUnit(), product.getDescription(), product.getProductPhoto(), product.getProductVideo(), product.getIsActive(), product.getIsDelete(), product.getStatus(), product.getHarvestDate(), product.getAverageRating(), product.getReviewCount());
    }@Override
    public List<AdminProductResponse> getProductsByFarmer(UUID farmerId) {
        return productRepository.findAll().stream().filter(product -> product.getFarmer() != null && product.getFarmer().getId().equals(farmerId)).map(this::mapToResponse).toList();
    }@Override
    public List<AdminProductResponse> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {throw new RuntimeException("Search keyword cannot be empty");}
        return productRepository
                .findByProductNameContainingIgnoreCase(keyword.trim()).stream().filter(product -> !Boolean.TRUE.equals(product.getIsDelete())).map(this::mapToResponse).toList();
    }@Override
    public List<AdminProductResponse> filterProducts(String category, Double minPrice, Double maxPrice) {
        List<Product> products;
        if (category != null && minPrice != null && maxPrice != null) {products = productRepository.findByCategoryIgnoreCaseAndPriceBetween(category, minPrice, maxPrice);
        } else if (category != null) {products = productRepository.findByCategoryIgnoreCase(category);
        } else if (minPrice != null && maxPrice != null) {products = productRepository.findByPriceBetween(minPrice, maxPrice);
        } else {products = productRepository.findAll();
        }return products.stream().filter(product -> !Boolean.TRUE.equals(product.getIsDelete())).map(this::mapToResponse).toList();
    }@Override
    public AdminProductResponse approveProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus("APPROVED");product.setIsActive(true);product.setIsDelete(false);productRepository.save(product);
        return mapToResponse(product);
    }@Override
    public AdminProductResponse rejectProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus("REJECTED");product.setIsActive(false);productRepository.save(product);
        return mapToResponse(product);
    }@Override
    public AdminProductResponse activateProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (Boolean.TRUE.equals(product.getIsDelete())) {throw new RuntimeException("Deleted product cannot be activated");
        }product.setIsActive(true);product.setStatus("ACTIVE");productRepository.save(product);
        return mapToResponse(product);
    }@Override
    public AdminProductResponse deactivateProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (Boolean.TRUE.equals(product.getIsDelete())) {throw new RuntimeException("Deleted product cannot be deactivated");
        }product.setIsActive(false);product.setStatus("INACTIVE");productRepository.save(product);
        return mapToResponse(product);
    }@Override
    public List<AdminProductResponse> getPendingProducts() {
        return productRepository.findByStatusIgnoreCase("PENDING").stream().filter(product -> !Boolean.TRUE.equals(product.getIsDelete())).map(this::mapToResponse).toList();
    }@Override
    public List<AdminProductResponse> getApprovedProducts() {
        return productRepository.findByStatusIgnoreCaseAndIsDeleteFalse("APPROVED").stream().map(this::mapToResponse).toList();
    }@Override
    public List<AdminProductResponse> getRejectedProducts() {
        return productRepository.findByStatusIgnoreCaseAndIsDeleteFalse("REJECTED").stream().map(this::mapToResponse).toList();
    }
}