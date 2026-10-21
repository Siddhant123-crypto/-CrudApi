package com.Siddhant.UserApp.scheduler;

import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Service.OrderService;
import com.Siddhant.UserApp.Service.PaymentService;
import com.Siddhant.UserApp.dto.SyncPaymentResponse;
import com.Siddhant.UserApp.enums.PaymentMethod;
import com.Siddhant.UserApp.enums.PaymentStatus;
import com.razorpay.RazorpayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StaleOrderCleanupScheduler — focuses on the Razorpay sync guard.
 * No Spring context needed; all collaborators are mocked.
 */
@ExtendWith(MockitoExtension.class)
class StaleOrderCleanupSchedulerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private StaleOrderCleanupScheduler scheduler;

    @BeforeEach
    void setUp() {
        // Default timeout: 30 minutes (same as prod default)
        ReflectionTestUtils.setField(scheduler, "pendingTimeoutMinutes", 30);
    }

    private Order buildStaleOrder(String razorpayOrderId) {
        Order order = new Order();
        order.setPaymentMethod(PaymentMethod.ONLINE);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId(razorpayOrderId);
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setCreatedOn(LocalDateTime.now().minusMinutes(45));
        return order;
    }

    // -----------------------------------------------------------------------
    // Test 1: Captured payment found → order NOT cancelled, stays PAID
    // -----------------------------------------------------------------------
    @Test
    void testCleanup_CapturedPaymentFound_OrderNotCancelledAndMarkedPaid() throws RazorpayException {
        Order order = buildStaleOrder("rzp_order_captured");

        when(orderRepository.findStaleUnpaidOnlineOrders(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(order));

        SyncPaymentResponse paidResponse = SyncPaymentResponse.builder()
                .orderId(order.getOrderId())
                .paymentStatus(PaymentStatus.PAID)
                .razorpayPaymentId("rzp_pay_abc123")
                .paid(true)
                .build();
        when(paymentService.syncPaymentStatusForScheduler(order)).thenReturn(paidResponse);

        scheduler.cleanupStaleUnpaidOrders();

        // syncPaymentStatusForScheduler was called
        verify(paymentService).syncPaymentStatusForScheduler(order);
        // autoCancelStaleOrder must NOT be called because payment is captured
        verify(orderService, never()).autoCancelStaleOrder(any(UUID.class), anyString());
    }

    // -----------------------------------------------------------------------
    // Test 2: No captured payment found → cancellation proceeds as before
    // -----------------------------------------------------------------------
    @Test
    void testCleanup_NoCapturedPayment_OrderCancelledAsNormal() throws RazorpayException {
        Order order = buildStaleOrder("rzp_order_nopay");

        when(orderRepository.findStaleUnpaidOnlineOrders(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(order));

        SyncPaymentResponse notPaidResponse = SyncPaymentResponse.builder()
                .orderId(order.getOrderId())
                .paymentStatus(PaymentStatus.PENDING)
                .razorpayPaymentId(null)
                .paid(false)
                .build();
        when(paymentService.syncPaymentStatusForScheduler(order)).thenReturn(notPaidResponse);
        when(orderService.autoCancelStaleOrder(order.getOrderId(), "Payment not completed in time"))
                .thenReturn(true);

        scheduler.cleanupStaleUnpaidOrders();

        verify(paymentService).syncPaymentStatusForScheduler(order);
        // autoCancelStaleOrder MUST be called because sync says not paid
        verify(orderService).autoCancelStaleOrder(eq(order.getOrderId()), eq("Payment not completed in time"));
    }

    // -----------------------------------------------------------------------
    // Test 3: Razorpay API throws → order NOT cancelled (temporary outage safety)
    // -----------------------------------------------------------------------
    @Test
    void testCleanup_RazorpayException_OrderNotCancelled() throws RazorpayException {
        Order order = buildStaleOrder("rzp_order_outage");

        when(orderRepository.findStaleUnpaidOnlineOrders(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(order));

        when(paymentService.syncPaymentStatusForScheduler(order))
                .thenThrow(new RazorpayException("Connection timeout"));

        scheduler.cleanupStaleUnpaidOrders();

        verify(paymentService).syncPaymentStatusForScheduler(order);
        // autoCancelStaleOrder must NOT be called — Razorpay outage must never cancel a possibly-paid order
        verify(orderService, never()).autoCancelStaleOrder(any(UUID.class), anyString());
    }
}
