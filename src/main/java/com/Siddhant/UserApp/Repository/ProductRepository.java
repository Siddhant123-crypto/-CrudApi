package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    List<Product> findByCategoryAndPriceBetween(
            String category,
            Double minPrice,
            Double maxPrice
    );
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
}
