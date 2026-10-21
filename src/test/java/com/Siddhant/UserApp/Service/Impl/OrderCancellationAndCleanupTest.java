package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.OrderItem;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Entity.ProductUnit;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.OrderItemRepository;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.OrderService;
import com.Siddhant.UserApp.dto.OrderResponse;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentMethod;
import com.Siddhant.UserApp.enums.PaymentStatus;
import com.Siddhant.UserApp.scheduler.StaleOrderCleanupScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class OrderCancellationAndCleanupTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StaleOrderCleanupScheduler staleOrderCleanupScheduler;

    private User testCustomer;
    private FarmerProfile testFarmer;
    private Product testProduct;

    @BeforeEach
    public void setUp() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);

        testCustomer = new User();
        testCustomer.setName("Cancel Test Customer");
        testCustomer.setEmail("canceltester_" + uniqueSuffix + "@example.com");
        testCustomer.setPassword("password123");
        testCustomer.setMobile("99" + uniqueSuffix.substring(0, 6));
        testCustomer.setRole(Role.CUSTOMER);
        testCustomer.setState("Maharashtra");
        testCustomer.setIsActive(true);
        testCustomer = userRepository.save(testCustomer);

        User farmerUser = new User();
        farmerUser.setName("Cancel Test Farmer");
        farmerUser.setEmail("farmer_" + uniqueSuffix + "@example.com");
        farmerUser.setPassword("password123");
        farmerUser.setMobile("88" + uniqueSuffix.substring(0, 6));
        farmerUser.setRole(Role.FARMER);
        farmerUser.setState("Maharashtra");
        farmerUser.setIsActive(true);
        farmerUser = userRepository.save(farmerUser);

        testFarmer = new FarmerProfile();
        testFarmer.setUser(farmerUser);
        testFarmer.setFarmName("Green Acres");
        testFarmer = farmerProfileRepository.save(testFarmer);

        testProduct = new Product();
        testProduct.setFarmer(testFarmer);
        testProduct.setProductName("Test Apples");
        testProduct.setPrice(100.0);
        testProduct.setQuantity(50.0);
        testProduct.setUnit(ProductUnit.KG);
        testProduct.setIsActive(true);
        testProduct.setIsDelete(false);
        testProduct = productRepository.save(testProduct);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testCustomer.getEmail(), null, Collections.emptyList())
        );
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Order createTestOrder(PaymentMethod pm, PaymentStatus ps, OrderStatus os,
                                  LocalDateTime createdOn, double itemQuantity) {
        Order order = new Order();
        order.setCustomer(testCustomer);
        order.setCustomerName(testCustomer.getName());
        order.setCustomerMobile(testCustomer.getMobile());
        order.setDeliveryAddress("123 Test Street");
        order.setVillage("Test Village");
        order.setPostalCode("411001");
        order.setState("Maharashtra");
        order.setPaymentMethod(pm);
        order.setPaymentStatus(ps);
        order.setStatus(os);
        order.setTotalAmount(BigDecimal.valueOf(100.0 * itemQuantity));
        order.setCreatedOn(createdOn != null ? createdOn : LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setFarmer(testFarmer);
        item.setProduct(testProduct);
        item.setProductName(testProduct.getProductName());
        item.setPrice(BigDecimal.valueOf(100.0));
        item.setQuantity(BigDecimal.valueOf(itemQuantity));
        item.setUnit("kg");
        item.setSubtotal(BigDecimal.valueOf(100.0 * itemQuantity));
        item.setStatus(os);

        order.setOrderItems(new ArrayList<>(List.of(item)));
        return orderRepository.save(order);
    }

    // ==================== TASK 1 TESTS ====================

    @Test
    public void testCancelPaidOnlineOrder_Throws409Conflict() {
        Order paidOnlineOrder = createTestOrder(
                PaymentMethod.ONLINE,
                PaymentStatus.PAID,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                2.0
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            orderService.cancelOrder(paidOnlineOrder.getOrderId(), "Customer requested cancel");
        });

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getReason()).contains("Paid online order cannot be cancelled automatically. Contact support for refund.");

        // Verify order is still PENDING and not cancelled in repository
        Order savedOrder = orderRepository.findById(paidOnlineOrder.getOrderId()).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    public void testCancelCodOrder_SucceedsAndRestoresStock() {
        double initialStock = testProduct.getQuantity();
        Order codOrder = createTestOrder(
                PaymentMethod.COD,
                PaymentStatus.PENDING,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                3.0
        );

        OrderResponse response = orderService.cancelOrder(codOrder.getOrderId(), "Customer changed mind");

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        // Verify repository state
        Order savedOrder = orderRepository.findById(codOrder.getOrderId()).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(savedOrder.getCancellationReason()).isEqualTo("Customer changed mind");

        // Verify product stock is restored
        Product reloadedProduct = productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertThat(reloadedProduct.getQuantity()).isEqualTo(initialStock + 3.0);
    }

    @Test
    public void testCancelUnpaidOnlineOrder_SucceedsAndRestoresStock() {
        double initialStock = testProduct.getQuantity();
        Order unpaidOnlineOrder = createTestOrder(
                PaymentMethod.ONLINE,
                PaymentStatus.PENDING,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                4.0
        );

        OrderResponse response = orderService.cancelOrder(unpaidOnlineOrder.getOrderId(), "Decided not to pay");

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Order savedOrder = orderRepository.findById(unpaidOnlineOrder.getOrderId()).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Product reloadedProduct = productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertThat(reloadedProduct.getQuantity()).isEqualTo(initialStock + 4.0);
    }

    @Test
    public void testCancelFailedOnlineOrder_SucceedsAndRestoresStock() {
        double initialStock = testProduct.getQuantity();
        Order failedOnlineOrder = createTestOrder(
                PaymentMethod.ONLINE,
                PaymentStatus.FAILED,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                2.5
        );

        OrderResponse response = orderService.cancelOrder(failedOnlineOrder.getOrderId(), "Payment failed cancellation");

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Order savedOrder = orderRepository.findById(failedOnlineOrder.getOrderId()).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Product reloadedProduct = productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertThat(reloadedProduct.getQuantity()).isEqualTo(initialStock + 2.5);
    }

    // ==================== TASK 2 TESTS ====================

    @Test
    public void testAutoCancelJob_CancelsOnlyStaleUnpaidOnlineOrdersAndRestoresStock() {
        double stockBeforeJob = testProduct.getQuantity();

        // 1. Stale unpaid ONLINE order (created 45 mins ago, cutoff is 30 mins) -> MUST be cancelled
        Order staleUnpaidOrder = createTestOrder(
                PaymentMethod.ONLINE,
                PaymentStatus.PENDING,
                OrderStatus.PENDING,
                LocalDateTime.now().minusMinutes(45),
                5.0
        );

        // 2. Young unpaid ONLINE order (created 10 mins ago) -> MUST NOT be cancelled
        Order youngUnpaidOrder = createTestOrder(
                PaymentMethod.ONLINE,
                PaymentStatus.PENDING,
                OrderStatus.PENDING,
                LocalDateTime.now().minusMinutes(10),
                2.0
        );

        // 3. Stale PAID ONLINE order (created 45 mins ago) -> MUST NOT be cancelled
        Order stalePaidOrder = createTestOrder(
                PaymentMethod.ONLINE,
                PaymentStatus.PAID,
                OrderStatus.PENDING,
                LocalDateTime.now().minusMinutes(45),
                3.0
        );

        // 4. Stale COD order (created 45 mins ago) -> MUST NOT be cancelled
        Order staleCodOrder = createTestOrder(
                PaymentMethod.COD,
                PaymentStatus.PENDING,
                OrderStatus.PENDING,
                LocalDateTime.now().minusMinutes(45),
                1.0
        );

        // Run the scheduled cleanup job
        staleOrderCleanupScheduler.cleanupStaleUnpaidOrders();

        // 1. Verify stale unpaid order IS cancelled and stock restored
        Order reloadedStaleOrder = orderRepository.findById(staleUnpaidOrder.getOrderId()).orElseThrow();
        assertThat(reloadedStaleOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(reloadedStaleOrder.getCancellationReason()).isEqualTo("Payment not completed in time");

        // 2. Verify young unpaid order stays PENDING
        Order reloadedYoungOrder = orderRepository.findById(youngUnpaidOrder.getOrderId()).orElseThrow();
        assertThat(reloadedYoungOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // 3. Verify stale PAID order stays PENDING
        Order reloadedPaidOrder = orderRepository.findById(stalePaidOrder.getOrderId()).orElseThrow();
        assertThat(reloadedPaidOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // 4. Verify stale COD order stays PENDING
        Order reloadedCodOrder = orderRepository.findById(staleCodOrder.getOrderId()).orElseThrow();
        assertThat(reloadedCodOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // Verify stock restoration only for the stale order (5.0 restored)
        Product reloadedProduct = productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertThat(reloadedProduct.getQuantity()).isEqualTo(stockBeforeJob + 5.0);
    }

    @Test
    public void testAutoCancel_RaceProtectionSkipsOrderThatBecamePaid() {
        // Order originally unpaid ONLINE
        Order order = createTestOrder(
                PaymentMethod.ONLINE,
                PaymentStatus.PENDING,
                OrderStatus.PENDING,
                LocalDateTime.now().minusMinutes(40),
                3.0
        );

        // Simulate concurrent payment (webhook/verification marks order as PAID)
        order.setPaymentStatus(PaymentStatus.PAID);
        orderRepository.save(order);

        // Attempt auto-cancellation
        boolean result = orderService.autoCancelStaleOrder(order.getOrderId(), "Payment not completed in time");

        // Must return false and leave status intact
        assertThat(result).isFalse();
        Order reloaded = orderRepository.findById(order.getOrderId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(reloaded.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
    }
}
