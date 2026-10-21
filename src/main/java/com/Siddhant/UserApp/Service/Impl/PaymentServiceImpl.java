package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.OrderItem;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Service.PaymentService;
import com.Siddhant.UserApp.dto.CreateOrderResponse;
import com.Siddhant.UserApp.enums.PaymentMethod;
import com.Siddhant.UserApp.enums.PaymentStatus;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import com.Siddhant.UserApp.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Value("${razorpay.route.enabled}")
    private boolean routeEnabled;

    @Value("${farm2home.commission.percent}")
    private int commissionPercent;

    public PaymentServiceImpl(RazorpayClient razorpayClient, OrderRepository orderRepository, UserRepository userRepository) {
        this.razorpayClient = razorpayClient;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private long toPaise(BigDecimal amount) {
        return amount.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    @Override
    @Transactional
    public CreateOrderResponse createRazorpayOrder(UUID orderId) {
        User currentUser = getLoggedInCustomer();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getCustomer().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is already paid");
        }
        
        if (order.getPaymentMethod() == PaymentMethod.COD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is set to COD");
        }

        long amountInPaise = toPaise(order.getTotalAmount());

        if (order.getRazorpayOrderId() != null && order.getPaymentStatus() == PaymentStatus.PENDING) {
            return CreateOrderResponse.builder()
                    .razorpayOrderId(order.getRazorpayOrderId())
                    .amount(amountInPaise)
                    .currency("INR")
                    .keyId(keyId)
                    .build();
        }

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + order.getOrderId().toString());

            if (routeEnabled) {
                boolean allHaveAccounts = true;
                for (OrderItem item : order.getOrderItems()) {
                    if (item.getFarmer().getRazorpayLinkedAccountId() == null || item.getFarmer().getRazorpayLinkedAccountId().isEmpty()) {
                        allHaveAccounts = false;
                        break;
                    }
                }
                
                if (allHaveAccounts) {
                    JSONArray transfers = new JSONArray();
                    long totalTransfers = 0;
                    for (OrderItem item : order.getOrderItems()) {
                        long itemTotalPaise = toPaise(item.getSubtotal());
                        long commissionPaise = (itemTotalPaise * commissionPercent) / 100;
                        long transferAmount = itemTotalPaise - commissionPaise;
                        
                        JSONObject transfer = new JSONObject();
                        transfer.put("account", item.getFarmer().getRazorpayLinkedAccountId());
                        transfer.put("amount", transferAmount);
                        transfer.put("currency", "INR");
                        
                        JSONObject notes = new JSONObject();
                        notes.put("farmer_id", item.getFarmer().getId().toString());
                        transfer.put("notes", notes);
                        
                        transfers.put(transfer);
                        totalTransfers += transferAmount;
                    }
                    if (totalTransfers > amountInPaise) {
                        logger.warn("Total transfers ({}) exceed order amount ({}). Skipping transfers.", totalTransfers, amountInPaise);
                    } else if (transfers.length() > 0) {
                        orderRequest.put("transfers", transfers);
                    }
                }
            }

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            order.setRazorpayOrderId(razorpayOrder.get("id"));
            order.setPaymentMethod(PaymentMethod.ONLINE);
            orderRepository.save(order);

            return CreateOrderResponse.builder()
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .amount(amountInPaise)
                    .currency("INR")
                    .keyId(keyId)
                    .build();

        } catch (RazorpayException e) {
            logger.error("Error creating razorpay order: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to payment gateway");
        }
    }

    @Override
    @Transactional
    public void verifyPayment(UUID orderId, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        User currentUser = getLoggedInCustomer();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getCustomer().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }
        
        if (!razorpayOrderId.equals(order.getRazorpayOrderId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order ID mismatch");
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return;
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);
            if (isValid) {
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setRazorpayPaymentId(razorpayPaymentId);
                order.setPaidAt(LocalDateTime.now());
                orderRepository.save(order);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment signature");
            }
        } catch (RazorpayException e) {
            logger.error("Error verifying payment", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to payment gateway");
        }
    }

    @Override
    @Transactional
    public void handleWebhookEvent(String rawBody, String signatureHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            logger.warn("Webhook secret not configured, ignoring webhook");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Webhook secret not configured");
        }
        
        try {
            boolean isValid = Utils.verifyWebhookSignature(rawBody, signatureHeader, webhookSecret);
            if (!isValid) {
                logger.warn("Invalid webhook signature");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature");
            }
            
            JSONObject payload = new JSONObject(rawBody);
            String event = payload.getString("event");
            
            if ("payment.captured".equals(event) || "payment.failed".equals(event)) {
                JSONObject paymentEntity = payload.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                String rzpayOrderId = paymentEntity.getString("order_id");
                
                orderRepository.findByRazorpayOrderId(rzpayOrderId).ifPresent(order -> {
                    if (order.getPaymentStatus() == PaymentStatus.PAID) {
                        return; // do not downgrade
                    }
                    if ("payment.captured".equals(event)) {
                        order.setPaymentStatus(PaymentStatus.PAID);
                        order.setRazorpayPaymentId(paymentEntity.getString("id"));
                        order.setPaidAt(LocalDateTime.now());
                    } else if ("payment.failed".equals(event)) {
                        order.setPaymentStatus(PaymentStatus.FAILED);
                    }
                    orderRepository.save(order);
                });
            }
            
        } catch (org.json.JSONException e) {
            logger.error("Error parsing webhook JSON", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON payload");
        } catch (RazorpayException e) {
            logger.error("Error processing webhook signature", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Signature verification failed");
        }
    }

    @Override
    @Transactional
    public com.Siddhant.UserApp.dto.SyncPaymentResponse syncPaymentStatus(UUID orderId) {
        User currentUser = getLoggedInCustomer();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getCustomer().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }

        try {
            return performRazorpaySync(order);
        } catch (RazorpayException e) {
            logger.error("Error syncing razorpay payment status: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to payment gateway");
        }
    }

    @Override
    @Transactional
    public com.Siddhant.UserApp.dto.SyncPaymentResponse syncPaymentStatusForScheduler(Order order) throws RazorpayException {
        // Reload order inside transaction to ensure fresh state
        Order freshOrder = orderRepository.findById(order.getOrderId())
                .orElse(null);
        if (freshOrder == null) {
            return com.Siddhant.UserApp.dto.SyncPaymentResponse.builder()
                    .orderId(order.getOrderId())
                    .paid(false)
                    .build();
        }
        // Propagates RazorpayException to caller (scheduler skips cancellation on outage)
        return performRazorpaySync(freshOrder);
    }

    /**
     * Core Razorpay sync logic shared by both the authenticated endpoint and the
     * internal scheduler call. Checks for a captured payment matching the order's
     * amount, currency, and Razorpay order ID. Marks the order PAID and saves it
     * when a match is found. Never downgrades a PAID order.
     *
     * @throws RazorpayException if the Razorpay API call itself fails
     */
    private com.Siddhant.UserApp.dto.SyncPaymentResponse performRazorpaySync(Order order) throws RazorpayException {
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return com.Siddhant.UserApp.dto.SyncPaymentResponse.builder()
                    .orderId(order.getOrderId())
                    .paymentStatus(order.getPaymentStatus())
                    .razorpayPaymentId(order.getRazorpayPaymentId())
                    .paid(true)
                    .build();
        }

        if (order.getRazorpayOrderId() == null || order.getRazorpayOrderId().isBlank()) {
            return com.Siddhant.UserApp.dto.SyncPaymentResponse.builder()
                    .orderId(order.getOrderId())
                    .paymentStatus(order.getPaymentStatus())
                    .razorpayPaymentId(order.getRazorpayPaymentId())
                    .paid(false)
                    .build();
        }

        // May throw RazorpayException — callers decide how to handle it
        java.util.List<com.razorpay.Payment> payments = razorpayClient.orders.fetchPayments(order.getRazorpayOrderId());
        long expectedAmountInPaise = toPaise(order.getTotalAmount());

        com.razorpay.Payment matchingCapturedPayment = null;
        if (payments != null) {
            for (com.razorpay.Payment payment : payments) {
                String status = payment.get("status");
                String rzpOrderId = payment.get("order_id");
                String currency = payment.get("currency");
                Object amountObj = payment.get("amount");

                long paymentAmount = 0L;
                if (amountObj instanceof Number) {
                    paymentAmount = ((Number) amountObj).longValue();
                } else if (amountObj instanceof String) {
                    try {
                        paymentAmount = Long.parseLong((String) amountObj);
                    } catch (NumberFormatException ignored) {}
                }

                if ("captured".equals(status)
                        && order.getRazorpayOrderId().equals(rzpOrderId)
                        && "INR".equalsIgnoreCase(currency)
                        && paymentAmount == expectedAmountInPaise) {
                    matchingCapturedPayment = payment;
                    break;
                }
            }
        }

        if (matchingCapturedPayment != null) {
            String paymentId = matchingCapturedPayment.get("id");
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setRazorpayPaymentId(paymentId);
            order.setPaidAt(LocalDateTime.now());
            orderRepository.save(order);

            return com.Siddhant.UserApp.dto.SyncPaymentResponse.builder()
                    .orderId(order.getOrderId())
                    .paymentStatus(PaymentStatus.PAID)
                    .razorpayPaymentId(paymentId)
                    .paid(true)
                    .build();
        }

        return com.Siddhant.UserApp.dto.SyncPaymentResponse.builder()
                .orderId(order.getOrderId())
                .paymentStatus(order.getPaymentStatus())
                .razorpayPaymentId(order.getRazorpayPaymentId())
                .paid(false)
                .build();
    }
}

