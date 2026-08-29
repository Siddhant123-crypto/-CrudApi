package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByCustomerUserId(UUID customerId);
    boolean existsByCustomerUserId(UUID customerId);
}
