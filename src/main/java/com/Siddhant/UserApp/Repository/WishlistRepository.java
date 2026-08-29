package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByCustomerUserId(UUID customerId);
    Optional<Wishlist> findByCustomerUserIdAndProductProductId(UUID customerId, UUID productId
    );
    void deleteByCustomerUserIdAndProductProductId(UUID customerId, UUID productId
    );
    boolean existsByCustomerUserIdAndProductProductId(UUID customerId, UUID productId
    );
}
