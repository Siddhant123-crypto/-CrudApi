package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.dto.ProductRequest;
import com.Siddhant.UserApp.dto.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponse saveProduct(ProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(UUID id);

    ProductResponse updateProduct(UUID id, ProductRequest request);

    String deleteProduct(UUID id);

    String uploadPhoto(MultipartFile photo);

    List<Product> searchProducts(String keyword);

    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    List<Product> filterByPrice(
            Double minPrice,
            Double maxPrice);

    List<Product> filterByCategoryAndPrice(
            String category,
            Double minPrice,
            Double maxPrice
    );
}