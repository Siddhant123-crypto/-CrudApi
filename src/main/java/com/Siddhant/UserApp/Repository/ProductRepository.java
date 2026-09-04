package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    List<Product> findByCategoryIgnoreCaseAndPriceBetween(String category, Double minPrice, Double maxPrice);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByFarmer(FarmerProfile farmer);
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByIsActiveTrueAndIsDeleteFalse();
    List<Product> findByFarmerAndIsActiveTrueAndIsDeleteFalse(FarmerProfile farmer);
    List<Product> findByProductNameContainingIgnoreCaseAndIsActiveTrueAndIsDeleteFalse(String keyword);
    List<Product> findByCategoryIgnoreCaseAndIsActiveTrueAndIsDeleteFalse(String category);
    List<Product> findByStatusIgnoreCase(String status);
    List<Product> findByStatusIgnoreCaseAndIsDeleteFalse(String status);
}