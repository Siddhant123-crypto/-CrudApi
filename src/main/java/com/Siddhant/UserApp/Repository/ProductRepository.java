package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // Search product by name
    List<Product> findByProductNameContainingIgnoreCase(
            String keyword
    );
    // Search by category and price range
    List<Product> findByCategoryIgnoreCaseAndPriceBetween(
            String category,
            Double minPrice,
            Double maxPrice
    );
    // Search by price range
    List<Product> findByPriceBetween(
            Double minPrice,
            Double maxPrice
    );
    // Get products of a particular farmer
    List<Product> findByFarmer(
            FarmerProfile farmer
    );
    // Search by category
    List<Product> findByCategoryIgnoreCase(
            String category
    );
    // Get only active and non-deleted products
    List<Product> findByIsActiveTrueAndIsDeleteFalse();
    // Get active products of a particular farmer
    List<Product> findByFarmerAndIsActiveTrueAndIsDeleteFalse(
            FarmerProfile farmer
    );
    // Search product name among active products
    List<Product> findByProductNameContainingIgnoreCaseAndIsActiveTrueAndIsDeleteFalse(
            String keyword
    );
    // Search category among active products
    List<Product> findByCategoryIgnoreCaseAndIsActiveTrueAndIsDeleteFalse(
            String category
    );
}