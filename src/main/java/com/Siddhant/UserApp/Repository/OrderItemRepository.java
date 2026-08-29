package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.OrderItem;
import com.Siddhant.UserApp.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrder(Order order);

    long countByFarmer_Id(UUID farmerId);

    long countByFarmer_IdAndStatus(UUID farmerId, OrderStatus status);
}