package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomer(User customer);
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId")
    List<Order> findDistinctByOrderItemsFarmer_Id(@Param("farmerId") UUID farmerId);
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId AND oi.status = :status")
    List<Order> findDistinctByOrderItemsFarmer_IdAndStatus(@Param("farmerId") UUID farmerId, @Param("status") OrderStatus status);
    long countByCustomer(User customer);
    long countByCustomerAndStatus(User customer, OrderStatus status);
    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.customer = :customer
            """)
    BigDecimal getTotalAmountByCustomer(@Param("customer") User customer);
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId")
    long countDistinctByOrderItemsFarmer_Id(@Param("farmerId") UUID farmerId);
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId AND oi.status = :status")
    long countDistinctByOrderItemsFarmer_IdAndStatus(@Param("farmerId") UUID farmerId, @Param("status") OrderStatus status);
    @Query("""
            SELECT COALESCE(SUM(oi.subtotal), 0)
            FROM OrderItem oi
            WHERE oi.farmer.id = :farmerId
            AND oi.status = com.Siddhant.UserApp.enums.OrderStatus.DELIVERED
            """)
    BigDecimal getTotalSalesByFarmer(@Param("farmerId") UUID farmerId);
    long countByStatus(OrderStatus status);
    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            """)
    BigDecimal getTotalOrderAmount();
    List<Order> findByCustomerAndStatusIn(User customer, List<OrderStatus> statuses);
    List<Order> findByOrderNumberContainingIgnoreCaseOrCustomerNameContainingIgnoreCaseOrCustomerMobileContaining(String orderNumber, String customerName, String customerMobile);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Order> findByStatusAndPaymentStatus(OrderStatus status, PaymentStatus paymentStatus);
    long countByPaymentStatus(PaymentStatus paymentStatus);
    List<Order> findTop5ByOrderByCreatedOnDesc();
}