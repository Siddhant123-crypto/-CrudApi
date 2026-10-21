package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, UUID> {
    Optional<OrderTracking> findByOrder_OrderIdAndFarmer_Id(UUID orderId, UUID farmerId);
    List<OrderTracking> findByOrder_OrderId(UUID orderId);
}
