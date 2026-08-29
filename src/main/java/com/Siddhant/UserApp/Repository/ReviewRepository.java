package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProductProductId(UUID productId);
    Optional<Review> findByCustomerUserIdAndProductProductIdAndOrderOrderId(UUID customerId, UUID productId, UUID orderId);
}
