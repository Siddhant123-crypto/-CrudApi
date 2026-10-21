package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
    List<Order> findByCustomer(User customer);
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId AND (o.paymentMethod IS NULL OR o.paymentMethod = com.Siddhant.UserApp.enums.PaymentMethod.COD OR o.paymentStatus = com.Siddhant.UserApp.enums.PaymentStatus.PAID)")
    List<Order> findDistinctByOrderItemsFarmer_Id(@Param("farmerId") UUID farmerId);
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId AND oi.status = :status AND (o.paymentMethod IS NULL OR o.paymentMethod = com.Siddhant.UserApp.enums.PaymentMethod.COD OR o.paymentStatus = com.Siddhant.UserApp.enums.PaymentStatus.PAID)")
    List<Order> findDistinctByOrderItemsFarmer_IdAndStatus(@Param("farmerId") UUID farmerId, @Param("status") OrderStatus status);
    long countByCustomer(User customer);
    long countByCustomerAndStatus(User customer, OrderStatus status);
    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.customer = :customer
            """)
    BigDecimal getTotalAmountByCustomer(@Param("customer") User customer);
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId AND (o.paymentMethod IS NULL OR o.paymentMethod = com.Siddhant.UserApp.enums.PaymentMethod.COD OR o.paymentStatus = com.Siddhant.UserApp.enums.PaymentStatus.PAID)")
    long countDistinctByOrderItemsFarmer_Id(@Param("farmerId") UUID farmerId);
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId AND oi.status = :status AND (o.paymentMethod IS NULL OR o.paymentMethod = com.Siddhant.UserApp.enums.PaymentMethod.COD OR o.paymentStatus = com.Siddhant.UserApp.enums.PaymentStatus.PAID)")
    long countDistinctByOrderItemsFarmer_IdAndStatus(@Param("farmerId") UUID farmerId, @Param("status") OrderStatus status);
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderItems oi WHERE oi.farmer.id = :farmerId AND oi.status = :status AND oi.farmerViewed = false AND (o.paymentMethod IS NULL OR o.paymentMethod = com.Siddhant.UserApp.enums.PaymentMethod.COD OR o.paymentStatus = com.Siddhant.UserApp.enums.PaymentStatus.PAID)")
    long countUnseenNewOrdersByFarmer(@Param("farmerId") UUID farmerId, @Param("status") OrderStatus status);
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

    @Query("""
            SELECT o FROM Order o
            WHERE o.paymentMethod = com.Siddhant.UserApp.enums.PaymentMethod.ONLINE
            AND (o.paymentStatus = com.Siddhant.UserApp.enums.PaymentStatus.PENDING OR o.paymentStatus = com.Siddhant.UserApp.enums.PaymentStatus.FAILED)
            AND o.status = com.Siddhant.UserApp.enums.OrderStatus.PENDING
            AND o.createdOn < :cutoffTime
            """)
    List<Order> findStaleUnpaidOnlineOrders(@Param("cutoffTime") LocalDateTime cutoffTime, Pageable pageable);
}