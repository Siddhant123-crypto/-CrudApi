package com.Siddhant.UserApp.Entity;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentMethod;
import com.Siddhant.UserApp.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;
    @Column(
            name = "order_number",
            nullable = false,
            unique = true
    )
    private String orderNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerProfile farmer;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String customerMobile;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;
    private String village;
    private String paymentId;
    @Column(length = 500)
    private String cancellationReason;
    private String postalCode;
    private String state;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    @Column(nullable = false)
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();
    @PrePersist
    protected void onCreate() {
        if (orderNumber == null || orderNumber.isBlank()) {
            orderNumber = "ORD-" + UUID.randomUUID();
        }
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        if (updatedOn == null) {
            updatedOn = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String transactionId;

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}