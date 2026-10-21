package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.OrderItem;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Service.OrderService;
import com.Siddhant.UserApp.dto.OrderItemRequest;
import com.Siddhant.UserApp.dto.OrderRequest;
import com.Siddhant.UserApp.dto.OrderResponse;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentMethod;
import com.Siddhant.UserApp.enums.PaymentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testFarmerVisibilityRule() {
        // Create User
        User customer = new User();
        customer.setName("Test Cust");
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        customer.setEmail("testcust" + uniqueSuffix + "@example.com");
        customer.setPassword("password");
        customer.setMobile("Cust" + uniqueSuffix.substring(uniqueSuffix.length() - 6));
        customer.setRole(Role.CUSTOMER);
        customer.setState("Test State");
        customer = userRepository.save(customer);

        // Create Farmer
        User farmerUser = new User();
        farmerUser.setName("Test Farmer User");
        farmerUser.setEmail("farmer" + uniqueSuffix + "@example.com");
        farmerUser.setPassword("password");
        farmerUser.setMobile("Farm" + uniqueSuffix.substring(uniqueSuffix.length() - 6));
        farmerUser.setRole(Role.FARMER);
        farmerUser.setState("Test State");
        farmerUser = userRepository.save(farmerUser);

        FarmerProfile farmer = new FarmerProfile();
        farmer.setUser(farmerUser);
        farmer.setFarmName("Farm");
        farmer = farmerProfileRepository.save(farmer);

        // Create Product
        Product product = new Product();
        product.setFarmer(farmer);
        product.setProductName("Product");
        product.setPrice(100.0);
        product.setQuantity(10.0);
        product.setUnit(com.Siddhant.UserApp.Entity.ProductUnit.KG);
        product = productRepository.save(product);
        
        // 1. NULL PaymentMethod Order (Legacy order -> Visible to Farmer)
        Order nullMethodOrder = createOrder(customer, farmer, product, null, PaymentStatus.PENDING);

        // 2. COD Order (Visible to Farmer)
        Order codOrder = createOrder(customer, farmer, product, PaymentMethod.COD, PaymentStatus.PENDING);
        
        // 3. ONLINE + PAID (Visible to Farmer)
        Order onlinePaidOrder = createOrder(customer, farmer, product, PaymentMethod.ONLINE, PaymentStatus.PAID);
        
        // 4. ONLINE + PENDING (Hidden from Farmer)
        Order onlinePendingOrder = createOrder(customer, farmer, product, PaymentMethod.ONLINE, PaymentStatus.PENDING);
        
        // 5. ONLINE + FAILED (Hidden from Farmer)
        Order onlineFailedOrder = createOrder(customer, farmer, product, PaymentMethod.ONLINE, PaymentStatus.FAILED);

        // Check Farmer Visibility Queries
        List<Order> farmerOrders = orderRepository.findDistinctByOrderItemsFarmer_Id(farmer.getId());
        assertThat(farmerOrders).contains(nullMethodOrder, codOrder, onlinePaidOrder);
        assertThat(farmerOrders).doesNotContain(onlinePendingOrder, onlineFailedOrder);

        // Check Farmer Status-filtered Query
        List<Order> farmerPendingOrders = orderRepository.findDistinctByOrderItemsFarmer_IdAndStatus(farmer.getId(), OrderStatus.PENDING);
        assertThat(farmerPendingOrders).contains(nullMethodOrder, codOrder, onlinePaidOrder);
        assertThat(farmerPendingOrders).doesNotContain(onlinePendingOrder, onlineFailedOrder);

        // Check Farmer Count Query
        long farmerCount = orderRepository.countDistinctByOrderItemsFarmer_Id(farmer.getId());
        assertThat(farmerCount).isEqualTo(3);

        // Check Customer Visibility (Everything visible)
        List<Order> customerOrders = orderRepository.findByCustomer(customer);
        assertThat(customerOrders).contains(nullMethodOrder, codOrder, onlinePaidOrder, onlinePendingOrder, onlineFailedOrder);
    }

    @Test
    public void testPlaceOrderDefaultingToCod() {
        // Create Customer
        User customer = new User();
        customer.setName("Order Placement Cust");
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        customer.setEmail("ordercust" + uniqueSuffix + "@example.com");
        customer.setPassword("password");
        customer.setMobile("Plc" + uniqueSuffix.substring(uniqueSuffix.length() - 6));
        customer.setRole(Role.CUSTOMER);
        customer.setState("Test State");
        customer.setIsActive(true);
        customer = userRepository.save(customer);

        // Create Farmer
        User farmerUser = new User();
        farmerUser.setName("Order Placement Farmer");
        farmerUser.setEmail("orderfarmer" + uniqueSuffix + "@example.com");
        farmerUser.setPassword("password");
        farmerUser.setMobile("Plf" + uniqueSuffix.substring(uniqueSuffix.length() - 6));
        farmerUser.setRole(Role.FARMER);
        farmerUser.setState("Test State");
        farmerUser.setIsActive(true);
        farmerUser = userRepository.save(farmerUser);

        FarmerProfile farmer = new FarmerProfile();
        farmer.setUser(farmerUser);
        farmer.setFarmName("Order Farm");
        farmer = farmerProfileRepository.save(farmer);

        // Create Product
        Product product = new Product();
        product.setFarmer(farmer);
        product.setProductName("Organic Apples");
        product.setPrice(150.0);
        product.setQuantity(50.0);
        product.setIsActive(true);
        product.setIsDelete(false);
        product.setUnit(com.Siddhant.UserApp.Entity.ProductUnit.KG);
        product = productRepository.save(product);

        // Authenticate as customer
        Authentication auth = new UsernamePasswordAuthenticationToken(customer.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Test 1: Order Request with missing (null) paymentMethod -> Defaults to COD and PENDING
        OrderRequest requestMissingPaymentMethod = new OrderRequest();
        requestMissingPaymentMethod.setDeliveryAddress("123 Green Valley Lane");
        requestMissingPaymentMethod.setVillage("Valley Village");
        requestMissingPaymentMethod.setPostalCode("411001");
        requestMissingPaymentMethod.setState("Maharashtra");
        requestMissingPaymentMethod.setPaymentMethod(null); // Explicitly null / missing

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(product.getProductId());
        item1.setQuantity(new BigDecimal("2"));
        requestMissingPaymentMethod.setItems(List.of(item1));

        OrderResponse response1 = orderService.createOrder(requestMissingPaymentMethod);
        assertThat(response1).isNotNull();
        assertThat(response1.getPaymentMethod()).isEqualTo(PaymentMethod.COD);
        assertThat(response1.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);

        // Test 2: Order Request with explicit ONLINE paymentMethod -> Sets ONLINE and PENDING
        OrderRequest requestOnline = new OrderRequest();
        requestOnline.setDeliveryAddress("456 Blue Horizon Rd");
        requestOnline.setVillage("Horizon Village");
        requestOnline.setPostalCode("411002");
        requestOnline.setState("Maharashtra");
        requestOnline.setPaymentMethod(PaymentMethod.ONLINE);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setProductId(product.getProductId());
        item2.setQuantity(new BigDecimal("1"));
        requestOnline.setItems(List.of(item2));

        OrderResponse response2 = orderService.createOrder(requestOnline);
        assertThat(response2).isNotNull();
        assertThat(response2.getPaymentMethod()).isEqualTo(PaymentMethod.ONLINE);
        assertThat(response2.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    private Order createOrder(User customer, FarmerProfile farmer, Product product, PaymentMethod pm, PaymentStatus ps) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setCustomerName(customer.getName());
        order.setCustomerMobile(customer.getMobile());
        order.setDeliveryAddress("Test Address");
        order.setPaymentMethod(pm);
        order.setPaymentStatus(ps);
        order.setTotalAmount(new BigDecimal("100"));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setFarmer(farmer);
        item.setProduct(product);
        item.setProductName("Product");
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(new BigDecimal("1"));
        item.setUnit("kg");
        item.setSubtotal(new BigDecimal("100"));
        item.setStatus(OrderStatus.PENDING);

        order.setOrderItems(List.of(item));
        return orderRepository.save(order);
    }
}
