package com.Siddhant.UserApp.scheduler;

import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Service.OrderService;
import com.Siddhant.UserApp.Service.PaymentService;
import com.Siddhant.UserApp.dto.SyncPaymentResponse;
import com.razorpay.RazorpayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StaleOrderCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(StaleOrderCleanupScheduler.class);

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;

    @Value("${farm2home.payment.pending-timeout-minutes:30}")
    private int pendingTimeoutMinutes;

    public StaleOrderCleanupScheduler(OrderRepository orderRepository,
                                      OrderService orderService,
                                      PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    /**
     * Periodically auto-cancels stale unpaid ONLINE orders.
     * Default runs every 5 minutes (300,000 ms).
     *
     * For any order that already has a Razorpay order ID, we sync with Razorpay
     * before cancelling. If a captured payment is found the order is marked PAID
     * and cancellation is skipped. If Razorpay itself is unavailable we also skip
     * the cancellation for this run — a temporary outage must never cancel a
     * possibly-paid order.
     */
    @Scheduled(fixedDelayString = "${farm2home.payment.cleanup-interval-ms:300000}", initialDelay = 60000)
    public void cleanupStaleUnpaidOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(pendingTimeoutMinutes);
        log.info("Starting stale unpaid online orders cleanup job. Cutoff: {}", cutoffTime);

        int totalCancelled = 0;
        int batchSize = 50;

        try {
            // Process in small batches
            List<Order> staleOrders = orderRepository.findStaleUnpaidOnlineOrders(cutoffTime, PageRequest.of(0, batchSize));
            if (staleOrders.isEmpty()) {
                log.info("No stale unpaid online orders found for cleanup.");
                return;
            }

            log.info("Found {} stale unpaid online order(s) to process.", staleOrders.size());

            for (Order order : staleOrders) {
                try {
                    // --- Razorpay sync guard ---
                    // Only orders that already started a Razorpay payment flow have an order ID.
                    if (order.getRazorpayOrderId() != null) {
                        SyncPaymentResponse syncRes;
                        try {
                            syncRes = paymentService.syncPaymentStatusForScheduler(order);
                        } catch (RazorpayException syncEx) {
                            // Razorpay API is unavailable — skip this order for safety.
                            // Log only order number and exception class; never log secrets.
                            log.warn("Razorpay sync failed for order {} ({}). Skipping cancellation this run.",
                                    order.getOrderNumber(), syncEx.getClass().getSimpleName());
                            continue;
                        }
                        if (syncRes.isPaid()) {
                            // Payment was captured — order already marked PAID inside sync; do not cancel.
                            log.info("Payment already captured for order {}. Skipping cancellation.", order.getOrderNumber());
                            continue;
                        }
                    }
                    // --- end sync guard ---

                    // Each cancellation runs in its own transaction via orderService.autoCancelStaleOrder
                    boolean cancelled = orderService.autoCancelStaleOrder(
                            order.getOrderId(),
                            "Payment not completed in time"
                    );

                    if (cancelled) {
                        totalCancelled++;
                        // Log only counts and order numbers (no personal data, no secrets)
                        log.info("Auto-cancelled stale unpaid order number: {}", order.getOrderNumber());
                    } else {
                        log.info("Skipped order number: {} (no longer eligible or already paid)", order.getOrderNumber());
                    }
                } catch (Exception ex) {
                    log.error("Failed to auto-cancel order number: {}", order.getOrderNumber(), ex);
                }
            }

            log.info("Completed stale orders cleanup job. Total processed: {}, successfully cancelled: {}",
                    staleOrders.size(), totalCancelled);
        } catch (Exception e) {
            log.error("Error occurred during stale orders cleanup job", e);
        }
    }
}
