package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.*;
import com.Siddhant.UserApp.Repository.*;
import com.Siddhant.UserApp.Service.OrderService;
import com.Siddhant.UserApp.dto.*;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentStatus;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            FarmerProfileRepository farmerProfileRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
    }

    // ==================== CREATE ORDER ====================

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        if (request == null)
            throw new RuntimeException("Order request cannot be null");

        if (request.getDeliveryAddress() == null ||
                request.getDeliveryAddress().trim().isEmpty())
            throw new RuntimeException("Delivery address is required");

        if (request.getItems() == null || request.getItems().isEmpty())
            throw new RuntimeException("Order must contain at least one product");

        User customer = getLoggedInCustomer();

        Order order = new Order();
        order.setCustomer(customer);
        order.setCustomerName(customer.getName());
        order.setCustomerMobile(customer.getMobile());

        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setVillage(request.getVillage());
        order.setPostalCode(request.getPostalCode());
        order.setState(request.getState());

        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            if (itemRequest == null || itemRequest.getProductId() == null)
                throw new RuntimeException("Product id is required");

            if (itemRequest.getQuantity() == null ||
                    itemRequest.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("Quantity must be greater than zero");

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                            "Product not found: " + itemRequest.getProductId()));

            if (!Boolean.TRUE.equals(product.getIsActive()))
                throw new RuntimeException(
                        "Product is not active: " + product.getProductName());

            if (Boolean.TRUE.equals(product.getIsDelete()))
                throw new RuntimeException(
                        "Product is deleted: " + product.getProductName());

            // ==================== FARMER ====================

            FarmerProfile productFarmer = product.getFarmer();

            if (productFarmer == null)
                throw new RuntimeException(
                        "Farmer not found for product: " + product.getProductName());

            // ==================== STOCK ====================

            if (product.getQuantity() == null)
                throw new RuntimeException(
                        "Stock information not available for product: "
                                + product.getProductName());

            BigDecimal availableQuantity =
                    BigDecimal.valueOf(product.getQuantity());

            BigDecimal requestedQuantity =
                    itemRequest.getQuantity();

            if (availableQuantity.compareTo(requestedQuantity) < 0)
                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getProductName()
                                + ". Available: " + availableQuantity
                                + ", Requested: " + requestedQuantity);

            // ==================== PRICE ====================

            BigDecimal price =
                    BigDecimal.valueOf(product.getPrice());

            BigDecimal subtotal =
                    price.multiply(requestedQuantity);

            // ==================== ORDER ITEM ====================

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setFarmer(productFarmer);   // IMPORTANT
            orderItem.setProduct(product);
            orderItem.setStatus(OrderStatus.PENDING);

            orderItem.setProductName(product.getProductName());
            orderItem.setPrice(price);
            orderItem.setQuantity(requestedQuantity);

            orderItem.setUnit(
                    product.getUnit() != null
                            ? product.getUnit().name()
                            : null
            );

            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);

            totalAmount = totalAmount.add(subtotal);

            // ==================== REDUCE STOCK ====================

            product.setQuantity(
                    availableQuantity
                            .subtract(requestedQuantity)
                            .doubleValue()
            );
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        // ==================== SAVE PRODUCTS ====================

        for (OrderItem item : orderItems)
            productRepository.save(item.getProduct());

        // ==================== SAVE ORDER ====================

        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    // ==================== CUSTOMER ORDER ====================

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {

        if (orderId == null)
            throw new RuntimeException("Order id is required");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new RuntimeException("User is not authenticated");
        }
        String email = authentication.getName();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        java.util.Optional<FarmerProfile> farmerOpt = farmerProfileRepository.findByUser_Email(email);

        if (farmerOpt.isPresent()) {
            FarmerProfile farmer = farmerOpt.get();
            boolean hasFarmerItem = order.getOrderItems().stream()
                    .anyMatch(item -> item.getFarmer().getId().equals(farmer.getId()));
            if (!hasFarmerItem) {
                throw new RuntimeException("You are not authorized to view this order");
            }
            return convertToResponse(order, farmer);
        } else {
            User customer = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (order.getCustomer() == null || !order.getCustomer().getUserId().equals(customer.getUserId())) {
                throw new RuntimeException("You are not authorized to view this order");
            }
            return convertToResponse(order);
        }
    }

    // ==================== FARMER ORDERS ====================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getFarmerOrders() {

        FarmerProfile farmer = getLoggedInFarmer();

        List<Order> orders =
                orderRepository.findDistinctByOrderItemsFarmer_Id(farmer.getId());

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders)
            responses.add(convertToResponse(order, farmer));

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getFarmerOrdersByStatus(
            OrderStatus status) {

        if (status == null)
            throw new RuntimeException("Order status is required");

        FarmerProfile farmer = getLoggedInFarmer();

        List<Order> orders =
                orderRepository.findDistinctByOrderItemsFarmer_IdAndStatus(
                        farmer.getId(), status);

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders)
            responses.add(convertToResponse(order, farmer));

        return responses;
    }

    // ==================== ACCEPT ORDER ====================

    @Override
    @Transactional
    public OrderResponse acceptOrder(UUID orderId) {

        if (orderId == null)
            throw new RuntimeException("Order id is required");

        FarmerProfile farmer = getLoggedInFarmer();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        checkFarmerOwnership(order, farmer);

        boolean anyUpdated = false;
        for (OrderItem item : order.getOrderItems()) {
            if (item.getFarmer().getId().equals(farmer.getId())) {
                if (item.getStatus() == OrderStatus.PENDING) {
                    item.setStatus(OrderStatus.CONFIRMED);
                    anyUpdated = true;
                }
            }
        }

        if (!anyUpdated)
            throw new RuntimeException("No pending items to accept for this farmer");

        updateOrderOverallStatus(order);

        return convertToResponse(orderRepository.save(order), farmer);
    }

    // ==================== UPDATE STATUS ====================

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            UUID orderId,
            OrderStatus newStatus) {

        if (orderId == null)
            throw new RuntimeException("Order id is required");

        if (newStatus == null)
            throw new RuntimeException("Order status is required");

        if (newStatus == OrderStatus.CANCELLED)
            throw new RuntimeException("Use cancel order API to cancel the order");

        if (newStatus == OrderStatus.CONFIRMED)
            throw new RuntimeException("Use accept order API to accept the order");

        FarmerProfile farmer = getLoggedInFarmer();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        checkFarmerOwnership(order, farmer);

        boolean anyUpdated = false;
        for (OrderItem item : order.getOrderItems()) {
            if (item.getFarmer().getId().equals(farmer.getId())) {
                OrderStatus currentStatus = item.getStatus();
                if (currentStatus == newStatus)
                    continue;

                boolean valid = false;

                switch (currentStatus) {
                    case CONFIRMED:
                        valid = newStatus == OrderStatus.PROCESSING;
                        break;

                    case PROCESSING:
                        valid = newStatus == OrderStatus.OUT_FOR_DELIVERY;
                        break;

                    case OUT_FOR_DELIVERY:
                        valid = newStatus == OrderStatus.DELIVERED;
                        break;

                    case PENDING:
                    case DELIVERED:
                    case CANCELLED:
                        valid = false;
                        break;
                }

                if (!valid)
                    throw new RuntimeException(
                            "Invalid status transition for item " + item.getProductName() + ": "
                                    + currentStatus + " -> " + newStatus);

                item.setStatus(newStatus);
                anyUpdated = true;
            }
        }

        if (anyUpdated) {
            updateOrderOverallStatus(order);
            orderRepository.save(order);
        }

        return convertToResponse(order, farmer);
    }

    // ==================== CUSTOMER CANCEL ====================

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String reason) {

        if (orderId == null)
            throw new RuntimeException("Order id is required");

        User customer = getLoggedInCustomer();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getCustomer() == null ||
                !order.getCustomer().getUserId()
                        .equals(customer.getUserId()))
            throw new RuntimeException(
                    "You are not authorized to cancel this order");

        OrderStatus status = order.getStatus();

        if (status == OrderStatus.CANCELLED)
            throw new RuntimeException("Order is already cancelled");

        if (status == OrderStatus.DELIVERED)
            throw new RuntimeException("Delivered order cannot be cancelled");

        if (status == OrderStatus.OUT_FOR_DELIVERY)
            throw new RuntimeException("Out for delivery order cannot be cancelled");

        for (OrderItem item : order.getOrderItems()) {
            if (item.getStatus() != OrderStatus.CANCELLED) {
                restoreItemStock(item);
                item.setStatus(OrderStatus.CANCELLED);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);

        if (reason != null && !reason.trim().isEmpty())
            order.setCancellationReason(reason.trim());

        return convertToResponse(orderRepository.save(order));
    }

    // ==================== UPDATE CANCELLATION REASON ====================

    @Override
    @Transactional
    public OrderResponse updateCancellationReason(
            UUID orderId,
            String reason) {

        if (reason == null || reason.trim().isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reason cannot be empty");

        User customer = getLoggedInCustomer();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"));

        if (order.getCustomer() == null ||
                !order.getCustomer().getUserId()
                        .equals(customer.getUserId()))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to update this order");

        if (order.getStatus() != OrderStatus.CANCELLED)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Can only update reason for cancelled orders");

        order.setCancellationReason(reason.trim());

        return convertToResponse(orderRepository.save(order));
    }

    // ==================== FARMER CANCEL ====================

    @Override
    @Transactional
    public FarmerCancelOrderResponse farmerCancelOrder(UUID orderId) {

        if (orderId == null)
            throw new RuntimeException("Order id is required");

        FarmerProfile farmer = getLoggedInFarmer();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        checkFarmerOwnership(order, farmer);

        boolean anyCancelled = false;
        for (OrderItem item : order.getOrderItems()) {
            if (item.getFarmer().getId().equals(farmer.getId())) {
                OrderStatus status = item.getStatus();
                if (status == OrderStatus.CANCELLED)
                    continue;

                if (status == OrderStatus.DELIVERED)
                    throw new RuntimeException("Delivered items cannot be cancelled");

                if (status == OrderStatus.OUT_FOR_DELIVERY)
                    throw new RuntimeException("Out for delivery items cannot be cancelled");

                restoreItemStock(item);
                item.setStatus(OrderStatus.CANCELLED);
                anyCancelled = true;
            }
        }

        if (!anyCancelled)
            throw new RuntimeException("No active items to cancel for this farmer");

        updateOrderOverallStatus(order);

        Order cancelledOrder = orderRepository.save(order);

        FarmerCancelOrderResponse response =
                new FarmerCancelOrderResponse();

        response.setMessage(
                "Order items cancelled successfully by farmer");

        response.setResponse(
                convertToResponse(cancelledOrder, farmer));

        return response;
    }

    // ==================== PAYMENT ====================

    @Override
    @Transactional
    public OrderResponse updatePayment(
            UUID orderId,
            PaymentRequest request) {

        if (orderId == null)
            throw new RuntimeException("Order id is required");

        if (request == null)
            throw new RuntimeException(
                    "Payment request cannot be null");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (request.getPaymentMethod() == null)
            throw new RuntimeException(
                    "Payment method is required");

        order.setPaymentMethod(request.getPaymentMethod());

        order.setPaymentStatus(
                request.getPaymentStatus() != null
                        ? request.getPaymentStatus()
                        : PaymentStatus.PENDING
        );

        order.setTransactionId(request.getTransactionId());

        return convertToResponse(orderRepository.save(order));
    }

    // ==================== FARMER DASHBOARD ====================

    @Override
    @Transactional(readOnly = true)
    public FarmerDashboardSummaryResponse getFarmerDashboardSummary() {
        FarmerProfile farmer = getLoggedInFarmer();
        UUID farmerId = farmer.getId();

        long totalOrders =
                orderRepository.countDistinctByOrderItemsFarmer_Id(farmerId);

        long newOrders =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.PENDING);

        long preparing =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.CONFIRMED);

        long ready =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.PROCESSING);

        long outForDelivery =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.OUT_FOR_DELIVERY);

        long delivered =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.DELIVERED);

        BigDecimal totalSales =
                orderRepository.getTotalSalesByFarmer(farmerId);

        if (totalSales == null)
            totalSales = BigDecimal.ZERO;

        FarmerDashboardSummaryResponse response =
                new FarmerDashboardSummaryResponse();

        response.setTotalOrders(totalOrders);
        response.setNewOrders(newOrders);
        response.setPreparing(preparing);
        response.setReady(ready);
        response.setOutForDelivery(outForDelivery);
        response.setDelivered(delivered);
        response.setTotalSales(totalSales);

        return response;
    }

    // ==================== CUSTOMER SUMMARY ====================

    @Override
    @Transactional(readOnly = true)
    public CustomerOrderSummaryResponse getCustomerOrderSummary() {

        User customer = getLoggedInCustomer();

        long totalOrders =
                orderRepository.countByCustomer(customer);

        long delivered =
                orderRepository.countByCustomerAndStatus(
                        customer, OrderStatus.DELIVERED);

        long cancelled =
                orderRepository.countByCustomerAndStatus(
                        customer, OrderStatus.CANCELLED);

        long activeOrders =
                totalOrders - delivered - cancelled;

        BigDecimal totalSpending =
                orderRepository.getTotalAmountByCustomer(customer);

        if (totalSpending == null)
            totalSpending = BigDecimal.ZERO;

        CustomerOrderSummaryResponse response =
                new CustomerOrderSummaryResponse();

        response.setTotalOrders(totalOrders);
        response.setActiveOrders(activeOrders);
        response.setDelivered(delivered);
        response.setCancelled(cancelled);
        response.setTotalSpending(totalSpending);

        return response;
    }

    // ==================== CUSTOMER ACTIVE ORDERS ====================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerMyOrders() {

        User customer = getLoggedInCustomer();

        List<OrderStatus> statuses = List.of(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.PROCESSING,
                OrderStatus.OUT_FOR_DELIVERY
        );

        List<Order> orders =
                orderRepository.findByCustomerAndStatusIn(
                        customer, statuses);

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders)
            responses.add(convertToResponse(order));

        return responses;
    }

    // ==================== CUSTOMER HISTORY ====================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrderHistory() {

        User customer = getLoggedInCustomer();

        List<OrderStatus> statuses = List.of(
                OrderStatus.DELIVERED,
                OrderStatus.CANCELLED
        );

        List<Order> orders =
                orderRepository.findByCustomerAndStatusIn(
                        customer, statuses);

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders)
            responses.add(convertToResponse(order));

        return responses;
    }

    // ==================== FARMER SUMMARY ====================

    @Override
    @Transactional(readOnly = true)
    public FarmerOrderSummaryResponse getFarmerOrderSummary() {

        FarmerProfile farmer = getLoggedInFarmer();
        UUID farmerId = farmer.getId();

        long totalOrders =
                orderRepository.countDistinctByOrderItemsFarmer_Id(farmerId);

        long newOrders =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.PENDING);

        long preparing =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.CONFIRMED);

        long ready =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.PROCESSING);

        long outForDelivery =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.OUT_FOR_DELIVERY);

        long delivered =
                orderRepository.countDistinctByOrderItemsFarmer_IdAndStatus(
                        farmerId, OrderStatus.DELIVERED);

        BigDecimal totalSales =
                orderRepository.getTotalSalesByFarmer(farmerId);

        if (totalSales == null)
            totalSales = BigDecimal.ZERO;

        FarmerOrderSummaryResponse response =
                new FarmerOrderSummaryResponse();

        response.setTotalOrders(totalOrders);
        response.setNewOrders(newOrders);
        response.setPreparing(preparing);
        response.setReady(ready);
        response.setOutForDelivery(outForDelivery);
        response.setDelivered(delivered);
        response.setTotalSales(totalSales);

        return response;
    }

    // ==================== GENERAL SUMMARY ====================

    @Override
    @Transactional(readOnly = true)
    public GeneralOrderSummaryResponse getGeneralOrderSummary() {

        long totalOrders = orderRepository.count();

        long pendingOrders =
                orderRepository.countByStatus(OrderStatus.PENDING);

        long confirmedOrders =
                orderRepository.countByStatus(OrderStatus.CONFIRMED);

        long deliveredOrders =
                orderRepository.countByStatus(OrderStatus.DELIVERED);

        long cancelledOrders =
                orderRepository.countByStatus(OrderStatus.CANCELLED);

        BigDecimal totalAmount =
                orderRepository.getTotalOrderAmount();

        if (totalAmount == null)
            totalAmount = BigDecimal.ZERO;

        GeneralOrderSummaryResponse response =
                new GeneralOrderSummaryResponse();

        response.setTotalOrders(totalOrders);
        response.setPendingOrders(pendingOrders);
        response.setConfirmedOrders(confirmedOrders);
        response.setDeliveredOrders(deliveredOrders);
        response.setCancelledOrders(cancelledOrders);
        response.setTotalAmount(totalAmount);

        return response;
    }

    // ==================== CUSTOMER ====================

    private User getLoggedInCustomer() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().isBlank())
            throw new RuntimeException(
                    "Customer is not authenticated");

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"));
    }

    // ==================== FARMER ====================

    private FarmerProfile getLoggedInFarmer() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().isBlank())
            throw new RuntimeException(
                    "Farmer is not authenticated");

        return farmerProfileRepository
                .findByUser_Email(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Farmer profile not found"));
    }

    // ==================== FARMER OWNERSHIP ====================

    private void checkFarmerOwnership(
            Order order,
            FarmerProfile farmer) {

        if (order == null)
            throw new RuntimeException("Order not found");

        if (farmer == null)
            throw new RuntimeException("Farmer not found");

        boolean hasFarmerItem = order.getOrderItems().stream()
                .anyMatch(item -> item.getFarmer().getId().equals(farmer.getId()));

        if (!hasFarmerItem)
            throw new RuntimeException(
                    "You are not authorized to update this order");
    }

    // ==================== RESTORE STOCK ====================

    private void restoreItemStock(OrderItem item) {
        if (item.getStatus() == OrderStatus.CANCELLED) {
            return;
        }
        Product product = item.getProduct();
        if (product != null) {
            double currentQuantity =
                    product.getQuantity() == null
                            ? 0
                            : product.getQuantity();

            double orderedQuantity =
                    item.getQuantity() == null
                            ? 0
                            : item.getQuantity().doubleValue();

            if (orderedQuantity > 0) {
                product.setQuantity(currentQuantity + orderedQuantity);
                productRepository.save(product);
            }
        }
    }

    private void restoreStock(Order order) {
        if (order == null ||
                order.getOrderItems() == null ||
                order.getOrderItems().isEmpty())
            return;

        for (OrderItem item : order.getOrderItems()) {
            restoreItemStock(item);
        }
    }

    // ==================== ORDER STATUS DERIVATION ====================

    private void updateOrderOverallStatus(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            order.setStatus(OrderStatus.PENDING);
            return;
        }

        boolean allCancelled = true;
        boolean allDelivered = true;
        boolean allDeliveredOrCancelled = true;
        boolean allConfirmedOrBeyond = true;
        boolean allProcessingOrBeyond = true;
        boolean allOutForDeliveryOrBeyond = true;

        for (OrderItem item : order.getOrderItems()) {
            if (item.getStatus() != OrderStatus.CANCELLED) {
                allCancelled = false;
            }
            if (item.getStatus() != OrderStatus.DELIVERED) {
                allDelivered = false;
            }
            if (item.getStatus() != OrderStatus.DELIVERED && item.getStatus() != OrderStatus.CANCELLED) {
                allDeliveredOrCancelled = false;
            }
            if (item.getStatus() == OrderStatus.PENDING || item.getStatus() == OrderStatus.CANCELLED) {
                if (item.getStatus() == OrderStatus.PENDING) {
                    allConfirmedOrBeyond = false;
                    allProcessingOrBeyond = false;
                    allOutForDeliveryOrBeyond = false;
                }
            }
            if (item.getStatus() == OrderStatus.CONFIRMED) {
                allProcessingOrBeyond = false;
                allOutForDeliveryOrBeyond = false;
            }
            if (item.getStatus() == OrderStatus.PROCESSING) {
                allOutForDeliveryOrBeyond = false;
            }
        }

        if (allCancelled) {
            order.setStatus(OrderStatus.CANCELLED);
        } else if (allDelivered || allDeliveredOrCancelled) {
            order.setStatus(OrderStatus.DELIVERED);
        } else if (allOutForDeliveryOrBeyond) {
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        } else if (allProcessingOrBeyond) {
            order.setStatus(OrderStatus.PROCESSING);
        } else if (allConfirmedOrBeyond) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            order.setStatus(OrderStatus.PENDING);
        }
    }

    // ==================== RESPONSE ====================

    private OrderResponse convertToResponse(Order order) {
        return convertToResponse(order, null);
    }

    private OrderResponse convertToResponse(Order order, FarmerProfile filterFarmer) {

        OrderResponse response = new OrderResponse();

        response.setOrderId(order.getOrderId());
        response.setOrderNumber(order.getOrderNumber());

        if (order.getCustomer() != null)
            response.setCustomerId(
                    order.getCustomer().getUserId());

        response.setCustomerName(order.getCustomerName());
        response.setCustomerMobile(order.getCustomerMobile());

        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setVillage(order.getVillage());
        response.setPostalCode(order.getPostalCode());
        response.setState(order.getState());

        response.setStatus(order.getStatus());

        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setTransactionId(order.getTransactionId());

        response.setCancellationReason(
                order.getCancellationReason());

        response.setCreatedOn(order.getCreatedOn());
        response.setUpdatedOn(order.getUpdatedOn());

        List<OrderItemResponse> itemResponses =
                new ArrayList<>();

        BigDecimal filterTotalAmount = BigDecimal.ZERO;
        UUID commonFarmerId = null;
        boolean singleFarmer = true;

        if (order.getOrderItems() != null) {

            for (OrderItem item : order.getOrderItems()) {

                if (filterFarmer != null && !item.getFarmer().getId().equals(filterFarmer.getId())) {
                    continue;
                }

                OrderItemResponse itemResponse =
                        new OrderItemResponse();

                if (item.getProduct() != null) {

                    itemResponse.setProductId(
                            item.getProduct().getProductId());

                    itemResponse.setImageUrl(
                            item.getProduct().getProductPhoto());
                }

                itemResponse.setProductName(item.getProductName());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setUnit(item.getUnit());
                itemResponse.setPrice(item.getPrice());
                itemResponse.setSubtotal(item.getSubtotal());
                itemResponse.setStatus(item.getStatus());

                if (item.getFarmer() != null) {
                    itemResponse.setFarmerId(item.getFarmer().getId());
                    if (item.getFarmer().getUser() != null && item.getFarmer().getUser().getName() != null) {
                        itemResponse.setFarmerName(item.getFarmer().getUser().getName());
                    } else {
                        // Explicitly fetch to resolve any Hibernate Proxy initialization issues with the lazy relation
                        FarmerProfile explicitFarmer = farmerProfileRepository.findById(item.getFarmer().getId()).orElse(null);
                        if (explicitFarmer != null && explicitFarmer.getUser() != null && explicitFarmer.getUser().getName() != null) {
                            itemResponse.setFarmerName(explicitFarmer.getUser().getName());
                        } else {
                            itemResponse.setFarmerName(item.getFarmer().getFarmName());
                        }
                    }

                    if (commonFarmerId == null) {
                        commonFarmerId = item.getFarmer().getId();
                    } else if (!commonFarmerId.equals(item.getFarmer().getId())) {
                        singleFarmer = false;
                    }
                }

                itemResponses.add(itemResponse);
                filterTotalAmount = filterTotalAmount.add(item.getSubtotal());
            }
        }

        response.setItems(itemResponses);

        if (filterFarmer != null) {
            response.setTotalAmount(filterTotalAmount);
            response.setFarmerId(filterFarmer.getId());
        } else {
            response.setTotalAmount(order.getTotalAmount());
            if (singleFarmer && commonFarmerId != null) {
                response.setFarmerId(commonFarmerId);
            } else {
                response.setFarmerId(null);
            }
        }

        return response;
    }
}
