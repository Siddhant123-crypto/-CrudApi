package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.OrderItem;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Entity.User;

import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Repository.UserRepository;

import com.Siddhant.UserApp.Service.OrderService;

import com.Siddhant.UserApp.dto.*;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.PaymentStatus;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            FarmerProfileRepository farmerProfileRepository
    ) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
    }

    // ==========================================================
    // CREATE ORDER
    // ==========================================================

    @Override
    @Transactional
    public OrderResponse createOrder(
            OrderRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Order request cannot be null"
            );
        }

        if (request.getDeliveryAddress() == null ||
                request.getDeliveryAddress().trim().isEmpty()) {

            throw new RuntimeException(
                    "Delivery address is required"
            );
        }

        if (request.getItems() == null ||
                request.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Order must contain at least one product"
            );
        }

        // ------------------------------------------------------
        // LOGGED-IN CUSTOMER
        // ------------------------------------------------------

        User customer =
                getLoggedInCustomer();

        // ------------------------------------------------------
        // CREATE ORDER
        // ------------------------------------------------------

        Order order =
                new Order();

        order.setCustomer(
                customer
        );

        order.setCustomerName(
                customer.getName()
        );

        order.setCustomerMobile(
                customer.getMobile()
        );

        // ------------------------------------------------------
        // DELIVERY ADDRESS
        // ------------------------------------------------------

        order.setDeliveryAddress(
                request.getDeliveryAddress().trim()
        );

        order.setVillage(
                request.getVillage()
        );

        order.setPostalCode(
                request.getPostalCode()
        );

        order.setState(
                request.getState()
        );

        // ------------------------------------------------------
        // INITIAL STATUS
        // ------------------------------------------------------

        order.setStatus(
                OrderStatus.PENDING
        );

        // ------------------------------------------------------
        // PAYMENT STATUS
        // ------------------------------------------------------

        order.setPaymentStatus(
                PaymentStatus.PENDING
        );

        BigDecimal totalAmount =
                BigDecimal.ZERO;

        List<OrderItem> orderItems =
                new ArrayList<>();

        FarmerProfile selectedFarmer =
                null;

        // ------------------------------------------------------
        // PROCESS ORDER ITEMS
        // ------------------------------------------------------

        for (OrderItemRequest itemRequest :
                request.getItems()) {

            if (itemRequest == null ||
                    itemRequest.getProductId() == null) {

                throw new RuntimeException(
                        "Product id is required"
                );
            }

            if (itemRequest.getQuantity() == null ||
                    itemRequest.getQuantity()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new RuntimeException(
                        "Quantity must be greater than zero"
                );
            }

            // --------------------------------------------------
            // FIND PRODUCT
            // --------------------------------------------------

            Product product =
                    productRepository
                            .findById(
                                    itemRequest.getProductId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product not found: "
                                                    + itemRequest.getProductId()
                                    )
                            );

            // --------------------------------------------------
            // ACTIVE CHECK
            // --------------------------------------------------

            if (!Boolean.TRUE.equals(
                    product.getIsActive()
            )) {

                throw new RuntimeException(
                        "Product is not active: "
                                + product.getProductName()
                );
            }

            // --------------------------------------------------
            // DELETE CHECK
            // --------------------------------------------------

            if (Boolean.TRUE.equals(
                    product.getIsDelete()
            )) {

                throw new RuntimeException(
                        "Product is deleted: "
                                + product.getProductName()
                );
            }

            // --------------------------------------------------
            // FARMER
            // --------------------------------------------------

            FarmerProfile productFarmer =
                    product.getFarmer();

            if (productFarmer == null) {

                throw new RuntimeException(
                        "Farmer not found for product: "
                                + product.getProductName()
                );
            }

            // --------------------------------------------------
            // ONE ORDER = ONE FARMER
            // --------------------------------------------------

            if (selectedFarmer == null) {

                selectedFarmer =
                        productFarmer;

            } else if (
                    !selectedFarmer.getId()
                            .equals(
                                    productFarmer.getId()
                            )
            ) {

                throw new RuntimeException(
                        "Products from different farmers must be placed in separate orders"
                );
            }

            // --------------------------------------------------
            // STOCK CHECK
            // --------------------------------------------------

            if (product.getQuantity() == null) {

                throw new RuntimeException(
                        "Stock information not available for product: "
                                + product.getProductName()
                );
            }

            BigDecimal availableQuantity =
                    BigDecimal.valueOf(
                            product.getQuantity()
                    );

            BigDecimal requestedQuantity =
                    itemRequest.getQuantity();

            if (availableQuantity.compareTo(
                    requestedQuantity
            ) < 0) {

                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getProductName()
                                + ". Available: "
                                + availableQuantity
                                + ", Requested: "
                                + requestedQuantity
                );
            }

            // --------------------------------------------------
            // PRICE
            // --------------------------------------------------

            BigDecimal price =
                    BigDecimal.valueOf(
                            product.getPrice()
                    );

            BigDecimal subtotal =
                    price.multiply(
                            requestedQuantity
                    );

            // --------------------------------------------------
            // CREATE ORDER ITEM
            // --------------------------------------------------

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(
                    order
            );

            orderItem.setProduct(
                    product
            );

            orderItem.setProductName(
                    product.getProductName()
            );

            orderItem.setPrice(
                    price
            );

            orderItem.setQuantity(
                    requestedQuantity
            );

            orderItem.setUnit(
                    product.getUnit() != null
                            ? product.getUnit().name()
                            : null
            );

            orderItem.setSubtotal(
                    subtotal
            );

            orderItems.add(
                    orderItem
            );

            // --------------------------------------------------
            // TOTAL
            // --------------------------------------------------

            totalAmount =
                    totalAmount.add(
                            subtotal
                    );

            // --------------------------------------------------
            // REDUCE STOCK
            // --------------------------------------------------

            BigDecimal updatedQuantity =
                    availableQuantity.subtract(
                            requestedQuantity
                    );

            product.setQuantity(
                    updatedQuantity.doubleValue()
            );
        }

        // ------------------------------------------------------
        // FARMER CHECK
        // ------------------------------------------------------

        if (selectedFarmer == null) {

            throw new RuntimeException(
                    "Farmer could not be identified"
            );
        }

        order.setFarmer(
                selectedFarmer
        );

        order.setTotalAmount(
                totalAmount
        );

        order.setOrderItems(
                orderItems
        );

        // ------------------------------------------------------
        // SAVE UPDATED STOCK
        // ------------------------------------------------------

        for (OrderItem item :
                orderItems) {

            productRepository.save(
                    item.getProduct()
            );
        }

        // ------------------------------------------------------
        // SAVE ORDER
        // ------------------------------------------------------

        Order savedOrder =
                orderRepository.save(
                        order
                );

        return convertToResponse(
                savedOrder
        );
    }


    // ==========================================================
    // CUSTOMER ORDER DETAILS
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(
            UUID orderId
    ) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order id is required"
            );
        }

        // ------------------------------------------------------
        // LOGGED-IN CUSTOMER
        // ------------------------------------------------------

        User customer =
                getLoggedInCustomer();

        // ------------------------------------------------------
        // FIND ORDER
        // ------------------------------------------------------

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        // ------------------------------------------------------
        // CHECK CUSTOMER OWNERSHIP
        // ------------------------------------------------------

        if (order.getCustomer() == null ||
                !order.getCustomer()
                        .getUserId()
                        .equals(
                                customer.getUserId()
                        )) {

            throw new RuntimeException(
                    "You are not authorized to view this order"
            );
        }

        return convertToResponse(
                order
        );
    }


    // ==========================================================
    // FARMER ORDERS
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getFarmerOrders() {

        FarmerProfile farmer =
                getLoggedInFarmer();

        List<Order> orders =
                orderRepository.findByFarmer_Id(
                        farmer.getId()
                );

        List<OrderResponse> responses =
                new ArrayList<>();

        for (Order order :
                orders) {

            responses.add(
                    convertToResponse(order)
            );
        }

        return responses;
    }


    // ==========================================================
    // FARMER ORDERS BY STATUS
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getFarmerOrdersByStatus(
            OrderStatus status
    ) {

        if (status == null) {

            throw new RuntimeException(
                    "Order status is required"
            );
        }

        FarmerProfile farmer =
                getLoggedInFarmer();

        List<Order> orders =
                orderRepository.findByFarmer_IdAndStatus(
                        farmer.getId(),
                        status
                );

        List<OrderResponse> responses =
                new ArrayList<>();

        for (Order order :
                orders) {

            responses.add(
                    convertToResponse(order)
            );
        }

        return responses;
    }


    // ==========================================================
    // FARMER ACCEPT ORDER
    // PENDING -> CONFIRMED
    // ==========================================================

    @Override
    @Transactional
    public OrderResponse acceptOrder(
            UUID orderId
    ) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order id is required"
            );
        }

        FarmerProfile farmer =
                getLoggedInFarmer();

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        checkFarmerOwnership(
                order,
                farmer
        );

        if (order.getStatus() !=
                OrderStatus.PENDING) {

            throw new RuntimeException(
                    "Only PENDING orders can be accepted"
            );
        }

        order.setStatus(
                OrderStatus.CONFIRMED
        );

        Order updatedOrder =
                orderRepository.save(
                        order
                );

        return convertToResponse(
                updatedOrder
        );
    }


    // ==========================================================
    // UPDATE ORDER STATUS
    // ==========================================================

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            UUID orderId,
            OrderStatus newStatus
    ) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order id is required"
            );
        }

        if (newStatus == null) {

            throw new RuntimeException(
                    "Order status is required"
            );
        }

        if (newStatus ==
                OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Use cancel order API to cancel the order"
            );
        }

        if (newStatus ==
                OrderStatus.CONFIRMED) {

            throw new RuntimeException(
                    "Use accept order API to accept the order"
            );
        }

        FarmerProfile farmer =
                getLoggedInFarmer();

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        checkFarmerOwnership(
                order,
                farmer
        );

        OrderStatus currentStatus =
                order.getStatus();

        if (currentStatus == newStatus) {

            throw new RuntimeException(
                    "Order is already in "
                            + currentStatus
                            + " status"
            );
        }

        boolean validTransition =
                false;

        switch (currentStatus) {

            case CONFIRMED:

                if (newStatus ==
                        OrderStatus.PROCESSING) {

                    validTransition = true;
                }

                break;

            case PROCESSING:

                if (newStatus ==
                        OrderStatus.OUT_FOR_DELIVERY) {

                    validTransition = true;
                }

                break;

            case OUT_FOR_DELIVERY:

                if (newStatus ==
                        OrderStatus.DELIVERED) {

                    validTransition = true;
                }

                break;

            case PENDING:
            case DELIVERED:
            case CANCELLED:

                validTransition = false;

                break;
        }

        if (!validTransition) {

            throw new RuntimeException(
                    "Invalid status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus
            );
        }

        order.setStatus(
                newStatus
        );

        Order updatedOrder =
                orderRepository.save(
                        order
                );

        return convertToResponse(
                updatedOrder
        );
    }


    // ==========================================================
    // CUSTOMER CANCEL ORDER
    // ==========================================================

    @Override
    @Transactional
    public OrderResponse cancelOrder(
            UUID orderId,
            String reason
    ) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order id is required"
            );
        }

        User customer =
                getLoggedInCustomer();

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        if (order.getCustomer() == null ||
                !order.getCustomer()
                        .getUserId()
                        .equals(
                                customer.getUserId()
                        )) {

            throw new RuntimeException(
                    "You are not authorized to cancel this order"
            );
        }

        OrderStatus currentStatus =
                order.getStatus();

        if (currentStatus ==
                OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Order is already cancelled"
            );
        }

        if (currentStatus ==
                OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "Delivered order cannot be cancelled"
            );
        }

        if (currentStatus ==
                OrderStatus.OUT_FOR_DELIVERY) {

            throw new RuntimeException(
                    "Out for delivery order cannot be cancelled"
            );
        }

        restoreStock(
                order
        );

        order.setStatus(
                OrderStatus.CANCELLED
        );
        if (reason != null && !reason.trim().isEmpty()) {
            order.setCancellationReason(reason.trim());
        }

        Order cancelledOrder =
                orderRepository.save(
                        order
                );

        return convertToResponse(
                cancelledOrder
        );
    }

    @Override
    @Transactional
    public OrderResponse updateCancellationReason(UUID orderId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reason cannot be empty");
        }
        User customer = getLoggedInCustomer();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"
                ));
        if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to update this order"
            );
        }
        if (order.getStatus() != OrderStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Can only update reason for cancelled orders"
            );
        }
        order.setCancellationReason(reason.trim());
        order = orderRepository.save(order);
        return convertToResponse(order);
    }


    // ==========================================================
    // FARMER CANCEL ORDER
    // ==========================================================

    @Override
    @Transactional
    public FarmerCancelOrderResponse farmerCancelOrder(
            UUID orderId
    ) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order id is required"
            );
        }

        FarmerProfile farmer =
                getLoggedInFarmer();

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        checkFarmerOwnership(
                order,
                farmer
        );

        OrderStatus currentStatus =
                order.getStatus();

        if (currentStatus ==
                OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Order is already cancelled"
            );
        }

        if (currentStatus ==
                OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "Delivered order cannot be cancelled"
            );
        }

        if (currentStatus ==
                OrderStatus.OUT_FOR_DELIVERY) {

            throw new RuntimeException(
                    "Out for delivery order cannot be cancelled"
            );
        }

        restoreStock(
                order
        );

        order.setStatus(
                OrderStatus.CANCELLED
        );

        Order cancelledOrder =
                orderRepository.save(
                        order
                );

        FarmerCancelOrderResponse response =
                new FarmerCancelOrderResponse();

        response.setMessage(
                "Order cancelled successfully by farmer"
        );

        response.setResponse(
                convertToResponse(
                        cancelledOrder
                )
        );

        return response;
    }


    // ==========================================================
    // UPDATE PAYMENT
    // ==========================================================

    @Override
    @Transactional
    public OrderResponse updatePayment(
            UUID orderId,
            PaymentRequest request
    ) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order id is required"
            );
        }

        if (request == null) {

            throw new RuntimeException(
                    "Payment request cannot be null"
            );
        }

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        if (request.getPaymentMethod() == null) {

            throw new RuntimeException(
                    "Payment method is required"
            );
        }

        order.setPaymentMethod(
                request.getPaymentMethod()
        );

        if (request.getPaymentStatus() != null) {

            order.setPaymentStatus(
                    request.getPaymentStatus()
            );

        } else {

            order.setPaymentStatus(
                    PaymentStatus.PENDING
            );
        }

        order.setTransactionId(
                request.getTransactionId()
        );

        Order updatedOrder =
                orderRepository.save(
                        order
                );

        return convertToResponse(
                updatedOrder
        );
    }


    // ==========================================================
    // FARMER DASHBOARD SUMMARY
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public FarmerDashboardSummaryResponse
    getFarmerDashboardSummary() {

        return null;
    }


    // ==========================================================
    // CUSTOMER ORDER SUMMARY
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public CustomerOrderSummaryResponse
    getCustomerOrderSummary() {

        User customer =
                getLoggedInCustomer();

        long totalOrders =
                orderRepository.countByCustomer(
                        customer
                );

        long delivered =
                orderRepository.countByCustomerAndStatus(
                        customer,
                        OrderStatus.DELIVERED
                );

        long cancelled =
                orderRepository.countByCustomerAndStatus(
                        customer,
                        OrderStatus.CANCELLED
                );

        long activeOrders =
                totalOrders
                        - delivered
                        - cancelled;

        BigDecimal totalSpending =
                orderRepository.getTotalAmountByCustomer(
                        customer
                );

        if (totalSpending == null) {

            totalSpending =
                    BigDecimal.ZERO;
        }

        CustomerOrderSummaryResponse response =
                new CustomerOrderSummaryResponse();

        response.setTotalOrders(
                totalOrders
        );

        response.setActiveOrders(
                activeOrders
        );

        response.setDelivered(
                delivered
        );

        response.setCancelled(
                cancelled
        );

        response.setTotalSpending(
                totalSpending
        );

        return response;
    }


    // ==========================================================
    // CUSTOMER MY ORDERS
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse>
    getCustomerMyOrders() {

        User customer =
                getLoggedInCustomer();

        List<OrderStatus> activeStatuses =
                List.of(
                        OrderStatus.PENDING,
                        OrderStatus.CONFIRMED,
                        OrderStatus.PROCESSING,
                        OrderStatus.OUT_FOR_DELIVERY
                );

        List<Order> orders =
                orderRepository.findByCustomerAndStatusIn(
                        customer,
                        activeStatuses
                );

        List<OrderResponse> responses =
                new ArrayList<>();

        for (Order order :
                orders) {

            responses.add(
                    convertToResponse(order)
            );
        }

        return responses;
    }


    // ==========================================================
    // CUSTOMER ORDER HISTORY
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse>
    getCustomerOrderHistory() {

        User customer =
                getLoggedInCustomer();

        List<OrderStatus> historyStatuses =
                List.of(
                        OrderStatus.DELIVERED,
                        OrderStatus.CANCELLED
                );

        List<Order> orders =
                orderRepository.findByCustomerAndStatusIn(
                        customer,
                        historyStatuses
                );

        List<OrderResponse> responses =
                new ArrayList<>();

        for (Order order :
                orders) {

            responses.add(
                    convertToResponse(order)
            );
        }

        return responses;
    }


    // ==========================================================
    // FARMER ORDER SUMMARY
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public FarmerOrderSummaryResponse
    getFarmerOrderSummary() {

        FarmerProfile farmer =
                getLoggedInFarmer();

        UUID farmerId =
                farmer.getId();

        long totalOrders =
                orderRepository.countByFarmer_Id(
                        farmerId
                );

        long newOrders =
                orderRepository.countByFarmer_IdAndStatus(
                        farmerId,
                        OrderStatus.PENDING
                );

        long preparing =
                orderRepository.countByFarmer_IdAndStatus(
                        farmerId,
                        OrderStatus.CONFIRMED
                );

        long ready =
                orderRepository.countByFarmer_IdAndStatus(
                        farmerId,
                        OrderStatus.PROCESSING
                );

        long outForDelivery =
                orderRepository.countByFarmer_IdAndStatus(
                        farmerId,
                        OrderStatus.OUT_FOR_DELIVERY
                );

        long delivered =
                orderRepository.countByFarmer_IdAndStatus(
                        farmerId,
                        OrderStatus.DELIVERED
                );

        BigDecimal totalSales =
                orderRepository.getTotalSalesByFarmer(
                        farmerId
                );

        if (totalSales == null) {

            totalSales =
                    BigDecimal.ZERO;
        }

        FarmerOrderSummaryResponse response =
                new FarmerOrderSummaryResponse();

        response.setTotalOrders(
                totalOrders
        );

        response.setNewOrders(
                newOrders
        );

        response.setPreparing(
                preparing
        );

        response.setReady(
                ready
        );

        response.setOutForDelivery(
                outForDelivery
        );

        response.setDelivered(
                delivered
        );

        response.setTotalSales(
                totalSales
        );

        return response;
    }


    // ==========================================================
    // GENERAL ORDER SUMMARY
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public GeneralOrderSummaryResponse
    getGeneralOrderSummary() {

        long totalOrders =
                orderRepository.count();

        long pendingOrders =
                orderRepository.countByStatus(
                        OrderStatus.PENDING
                );

        long confirmedOrders =
                orderRepository.countByStatus(
                        OrderStatus.CONFIRMED
                );

        long deliveredOrders =
                orderRepository.countByStatus(
                        OrderStatus.DELIVERED
                );

        long cancelledOrders =
                orderRepository.countByStatus(
                        OrderStatus.CANCELLED
                );

        BigDecimal totalAmount =
                orderRepository.getTotalOrderAmount();

        if (totalAmount == null) {

            totalAmount =
                    BigDecimal.ZERO;
        }

        GeneralOrderSummaryResponse response =
                new GeneralOrderSummaryResponse();

        response.setTotalOrders(
                totalOrders
        );

        response.setPendingOrders(
                pendingOrders
        );

        response.setConfirmedOrders(
                confirmedOrders
        );

        response.setDeliveredOrders(
                deliveredOrders
        );

        response.setCancelledOrders(
                cancelledOrders
        );

        response.setTotalAmount(
                totalAmount
        );

        return response;
    }


    // ==========================================================
    // LOGGED-IN CUSTOMER
    // ==========================================================

    private User getLoggedInCustomer() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().isBlank()) {

            throw new RuntimeException(
                    "Customer is not authenticated"
            );
        }

        return userRepository
                .findByEmail(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"
                        )
                );
    }


    // ==========================================================
    // LOGGED-IN FARMER
    // ==========================================================

    private FarmerProfile getLoggedInFarmer() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().isBlank()) {

            throw new RuntimeException(
                    "Farmer is not authenticated"
            );
        }

        return farmerProfileRepository
                .findByUser_Email(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Farmer profile not found"
                        )
                );
    }


    // ==========================================================
    // CHECK FARMER OWNERSHIP
    // ==========================================================

    private void checkFarmerOwnership(
            Order order,
            FarmerProfile farmer
    ) {

        if (order == null) {

            throw new RuntimeException(
                    "Order not found"
            );
        }

        if (farmer == null) {

            throw new RuntimeException(
                    "Farmer not found"
            );
        }

        if (order.getFarmer() == null) {

            throw new RuntimeException(
                    "Farmer is not assigned to this order"
            );
        }

        if (!order.getFarmer()
                .getId()
                .equals(
                        farmer.getId()
                )) {

            throw new RuntimeException(
                    "You are not authorized to update this order"
            );
        }
    }


    // ==========================================================
    // RESTORE STOCK
    // ==========================================================

    private void restoreStock(
            Order order
    ) {

        if (order == null) {

            return;
        }

        if (order.getOrderItems() == null ||
                order.getOrderItems().isEmpty()) {

            return;
        }

        for (OrderItem item :
                order.getOrderItems()) {

            if (item == null) {

                continue;
            }

            Product product =
                    item.getProduct();

            if (product == null) {

                continue;
            }

            double currentQuantity =
                    product.getQuantity() == null
                            ? 0
                            : product.getQuantity();

            double orderedQuantity =
                    item.getQuantity() == null
                            ? 0
                            : item.getQuantity()
                            .doubleValue();

            if (orderedQuantity <= 0) {

                continue;
            }

            product.setQuantity(
                    currentQuantity
                            + orderedQuantity
            );

            productRepository.save(
                    product
            );
        }
    }


    // ==========================================================
    // ENTITY -> RESPONSE
    // ==========================================================

    private OrderResponse convertToResponse(
            Order order
    ) {

        OrderResponse response =
                new OrderResponse();

        // ------------------------------------------------------
        // ORDER DETAILS
        // ------------------------------------------------------

        response.setOrderId(
                order.getOrderId()
        );

        response.setOrderNumber(
                order.getOrderNumber()
        );

        // ------------------------------------------------------
        // CUSTOMER
        // ------------------------------------------------------

        if (order.getCustomer() != null) {

            response.setCustomerId(
                    order.getCustomer()
                            .getUserId()
            );
        }

        // ------------------------------------------------------
        // FARMER
        // ------------------------------------------------------

        if (order.getFarmer() != null) {

            response.setFarmerId(
                    order.getFarmer()
                            .getId()
            );
        }

        // ------------------------------------------------------
        // CUSTOMER DETAILS
        // ------------------------------------------------------

        response.setCustomerName(
                order.getCustomerName()
        );

        response.setCustomerMobile(
                order.getCustomerMobile()
        );

        // ------------------------------------------------------
        // DELIVERY DETAILS
        // ------------------------------------------------------

        response.setDeliveryAddress(
                order.getDeliveryAddress()
        );

        response.setVillage(
                order.getVillage()
        );

        response.setPostalCode(
                order.getPostalCode()
        );

        response.setState(
                order.getState()
        );

        // ------------------------------------------------------
        // ORDER DETAILS
        // ------------------------------------------------------

        response.setTotalAmount(
                order.getTotalAmount()
        );

        response.setStatus(
                order.getStatus()
        );

        // ------------------------------------------------------
        // PAYMENT DETAILS
        // ------------------------------------------------------

        response.setPaymentStatus(
                order.getPaymentStatus()
        );

        response.setPaymentMethod(
                order.getPaymentMethod()
        );

        response.setTransactionId(
                order.getTransactionId()
        );

        response.setCancellationReason(
                order.getCancellationReason()
        );

        // ------------------------------------------------------
        // DATE / TIME
        // ------------------------------------------------------

        response.setCreatedOn(
                order.getCreatedOn()
        );

        response.setUpdatedOn(
                order.getUpdatedOn()
        );

        // ------------------------------------------------------
        // ORDER ITEMS
        // ------------------------------------------------------

        List<OrderItemResponse> itemResponses =
                new ArrayList<>();

        if (order.getOrderItems() != null) {

            for (OrderItem item :
                    order.getOrderItems()) {

                OrderItemResponse itemResponse =
                        new OrderItemResponse();

                if (item.getProduct() != null) {

                    itemResponse.setProductId(
                            item.getProduct()
                                    .getProductId()
                    );
                    itemResponse.setImageUrl(
                            item.getProduct().getProductPhoto()
                    );
                }

                itemResponse.setProductName(
                        item.getProductName()
                );

                itemResponse.setQuantity(
                        item.getQuantity()
                );

                itemResponse.setUnit(
                        item.getUnit()
                );

                itemResponse.setPrice(
                        item.getPrice()
                );

                itemResponse.setSubtotal(
                        item.getSubtotal()
                );

                itemResponses.add(
                        itemResponse
                );
            }
        }

        response.setItems(
                itemResponses
        );

        return response;
    }
}