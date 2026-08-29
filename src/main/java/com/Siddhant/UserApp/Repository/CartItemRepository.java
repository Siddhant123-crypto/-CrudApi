package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartCartIdAndProductProductId(UUID cartId, UUID productId);
    boolean existsByCartCartIdAndProductProductId(UUID cartId, UUID productId);
    void deleteByCartCartIdAndProductProductId(UUID cartId, UUID productId);
}
