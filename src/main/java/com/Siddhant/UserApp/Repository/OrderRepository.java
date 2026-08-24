package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // ==========================================================
    // CUSTOMER ORDERS
    // ==========================================================

    List<Order> findByCustomer(User customer);

    // ==========================================================
    // FARMER ORDERS
    // ==========================================================

    List<Order> findByFarmer_Id(UUID farmerId);

    List<Order> findByFarmer_IdAndStatus(
            UUID farmerId,
            OrderStatus status
    );

    // ==========================================================
    // CUSTOMER SUMMARY
    // ==========================================================

    long countByCustomer(User customer);

    long countByCustomerAndStatus(
            User customer,
            OrderStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.customer = :customer
            """)
    BigDecimal getTotalAmountByCustomer(
            @Param("customer") User customer
    );

    // ==========================================================
    // FARMER SUMMARY
    // ==========================================================

    long countByFarmer_Id(UUID farmerId);

    long countByFarmer_IdAndStatus(
            UUID farmerId,
            OrderStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.farmer.id = :farmerId
            AND o.status = com.Siddhant.UserApp.enums.OrderStatus.DELIVERED
            """)
    BigDecimal getTotalSalesByFarmer(
            @Param("farmerId") UUID farmerId
    );

    // ==========================================================
    // GENERAL SUMMARY
    // ==========================================================

    long countByStatus(OrderStatus status);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            """)
    BigDecimal getTotalOrderAmount();

    List<Order> findByCustomerAndStatusIn(
            User customer,
            List<OrderStatus> statuses
    );

}