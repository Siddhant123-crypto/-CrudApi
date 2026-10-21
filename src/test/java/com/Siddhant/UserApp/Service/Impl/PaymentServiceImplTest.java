package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.enums.PaymentStatus;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private RazorpayClient razorpayClient;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private MockedStatic<Utils> mockedUtils;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "keyId", "dummy_key_id");
        ReflectionTestUtils.setField(paymentService, "keySecret", "dummy_key_secret");
        ReflectionTestUtils.setField(paymentService, "webhookSecret", "dummy_webhook");
        
        mockedUtils = mockStatic(Utils.class);
    }

    @AfterEach
    void tearDown() {
        mockedUtils.close();
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(User user) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(authentication.getPrincipal()).thenReturn("testPrincipal");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testCreateRazorpayOrder_AlreadyPaid() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PAID);

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        assertThrows(ResponseStatusException.class, () -> paymentService.createRazorpayOrder(order.getOrderId()));
    }

    @Test
    void testVerifyPayment_OrderIdMismatch() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setRazorpayOrderId("rzp_order_123");
        order.setPaymentStatus(PaymentStatus.PENDING);

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        assertThrows(ResponseStatusException.class, () -> paymentService.verifyPayment(
                order.getOrderId(), "rzp_order_WRONG", "rzp_pay_123", "sig"
        ));
    }

    @Test
    void testVerifyPayment_InvalidSignature_DoesNotChangeStatus() throws RazorpayException {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setRazorpayOrderId("rzp_order_123");
        order.setPaymentStatus(PaymentStatus.PENDING);

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        
        mockedUtils.when(() -> Utils.verifyPaymentSignature(any(JSONObject.class), anyString())).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> paymentService.verifyPayment(
                order.getOrderId(), "rzp_order_123", "rzp_pay_123", "sig"
        ));

        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        verify(orderRepository, never()).save(order);
    }

    @Test
    void testVerifyPayment_Idempotent() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setRazorpayOrderId("rzp_order_123");
        order.setPaymentStatus(PaymentStatus.PAID);

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        // It should return early without verifying signature or saving
        assertDoesNotThrow(() -> paymentService.verifyPayment(
                order.getOrderId(), "rzp_order_123", "rzp_pay_123", "sig"
        ));
        
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testHandleWebhook_DoesNotDowngradePaidOrder() throws RazorpayException {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setRazorpayOrderId("rzp_order_123");
        order.setPaymentStatus(PaymentStatus.PAID);

        when(orderRepository.findByRazorpayOrderId("rzp_order_123")).thenReturn(Optional.of(order));
        
        mockedUtils.when(() -> Utils.verifyWebhookSignature(anyString(), anyString(), anyString())).thenReturn(true);

        String payload = "{\"event\":\"payment.failed\",\"payload\":{\"payment\":{\"entity\":{\"order_id\":\"rzp_order_123\"}}}}";
        
        paymentService.handleWebhookEvent(payload, "valid_sig");

        // Status should still be PAID
        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());
        // Verify save was not called or called with PAID
        verify(orderRepository, never()).save(order);
    }

    @Test
    void testHandleWebhook_BlankSecret() {
        ReflectionTestUtils.setField(paymentService, "webhookSecret", "");
        assertThrows(ResponseStatusException.class, () -> paymentService.handleWebhookEvent("{}", "sig"));
    }

    @Test
    void testCreateRazorpayOrder_TwiceReturnsSameOrder() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId("existing_rzp_order_123");
        order.setTotalAmount(new BigDecimal("123.45"));

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        com.Siddhant.UserApp.dto.CreateOrderResponse response = paymentService.createRazorpayOrder(order.getOrderId());
        
        assertEquals("existing_rzp_order_123", response.getRazorpayOrderId());
        assertEquals(12345L, response.getAmount());
        assertEquals("dummy_key_id", response.getKeyId());
        
        // Ensure RazorpayClient is never called
        verifyNoInteractions(razorpayClient);
    }

    @Test
    void testToPaiseRounding() throws Exception {
        java.lang.reflect.Method method = PaymentServiceImpl.class.getDeclaredMethod("toPaise", BigDecimal.class);
        method.setAccessible(true);
        
        // 123.456 -> 123.46 -> 12346 paise
        long paise = (long) method.invoke(paymentService, new BigDecimal("123.456"));
        assertEquals(12346L, paise);
        
        // 100.124 -> 100.12 -> 10012 paise
        long paise2 = (long) method.invoke(paymentService, new BigDecimal("100.124"));
        assertEquals(10012L, paise2);
    }

    @Test
    void testSyncPaymentStatus_AlreadyPaid_ReturnsWithoutCallingRazorpay() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setRazorpayOrderId("rzp_order_123");
        order.setRazorpayPaymentId("rzp_pay_already");

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        com.Siddhant.UserApp.dto.SyncPaymentResponse res = paymentService.syncPaymentStatus(order.getOrderId());

        assertTrue(res.isPaid());
        assertEquals(PaymentStatus.PAID, res.getPaymentStatus());
        assertEquals("rzp_pay_already", res.getRazorpayPaymentId());
        verifyNoInteractions(razorpayClient);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testSyncPaymentStatus_NoRazorpayOrderId_ReturnsUnchanged() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId(null);

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        com.Siddhant.UserApp.dto.SyncPaymentResponse res = paymentService.syncPaymentStatus(order.getOrderId());

        assertFalse(res.isPaid());
        assertEquals(PaymentStatus.PENDING, res.getPaymentStatus());
        assertNull(res.getRazorpayPaymentId());
        verifyNoInteractions(razorpayClient);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testSyncPaymentStatus_CapturedPayment_MatchingAmount_MarksPaid() throws Exception {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId("rzp_order_match");
        order.setTotalAmount(new BigDecimal("250.00")); // 25000 paise

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        JSONObject paymentJson = new JSONObject();
        paymentJson.put("id", "rzp_pay_success_1");
        paymentJson.put("order_id", "rzp_order_match");
        paymentJson.put("status", "captured");
        paymentJson.put("currency", "INR");
        paymentJson.put("amount", 25000);
        com.razorpay.Payment payment = new com.razorpay.Payment(paymentJson);

        com.razorpay.OrderClient orderClient = mock(com.razorpay.OrderClient.class);
        razorpayClient.orders = orderClient;
        when(orderClient.fetchPayments("rzp_order_match")).thenReturn(java.util.List.of(payment));

        com.Siddhant.UserApp.dto.SyncPaymentResponse res = paymentService.syncPaymentStatus(order.getOrderId());

        assertTrue(res.isPaid());
        assertEquals(PaymentStatus.PAID, res.getPaymentStatus());
        assertEquals("rzp_pay_success_1", res.getRazorpayPaymentId());
        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());
        assertEquals("rzp_pay_success_1", order.getRazorpayPaymentId());
        assertNotNull(order.getPaidAt());
        verify(orderRepository).save(order);
    }

    @Test
    void testSyncPaymentStatus_CapturedPayment_DifferentAmount_DoesNotMarkPaid() throws Exception {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId("rzp_order_diff");
        order.setTotalAmount(new BigDecimal("250.00")); // 25000 paise

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        JSONObject paymentJson = new JSONObject();
        paymentJson.put("id", "rzp_pay_diff_amount");
        paymentJson.put("order_id", "rzp_order_diff");
        paymentJson.put("status", "captured");
        paymentJson.put("currency", "INR");
        paymentJson.put("amount", 10000); // 10000 != 25000
        com.razorpay.Payment payment = new com.razorpay.Payment(paymentJson);

        com.razorpay.OrderClient orderClient = mock(com.razorpay.OrderClient.class);
        razorpayClient.orders = orderClient;
        when(orderClient.fetchPayments("rzp_order_diff")).thenReturn(java.util.List.of(payment));

        com.Siddhant.UserApp.dto.SyncPaymentResponse res = paymentService.syncPaymentStatus(order.getOrderId());

        assertFalse(res.isPaid());
        assertEquals(PaymentStatus.PENDING, res.getPaymentStatus());
        assertNull(res.getRazorpayPaymentId());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testSyncPaymentStatus_NoCapturedPayment_LeavesOrderUnchanged() throws Exception {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId("rzp_order_empty");
        order.setTotalAmount(new BigDecimal("100.00"));

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        com.razorpay.OrderClient orderClient = mock(com.razorpay.OrderClient.class);
        razorpayClient.orders = orderClient;
        when(orderClient.fetchPayments("rzp_order_empty")).thenReturn(java.util.Collections.emptyList());

        com.Siddhant.UserApp.dto.SyncPaymentResponse res = paymentService.syncPaymentStatus(order.getOrderId());

        assertFalse(res.isPaid());
        assertEquals(PaymentStatus.PENDING, res.getPaymentStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testSyncPaymentStatus_FailedPayments_NeverMarkOrderFailed() throws Exception {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId("rzp_order_failed_pay");
        order.setTotalAmount(new BigDecimal("100.00"));

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        JSONObject paymentJson = new JSONObject();
        paymentJson.put("id", "rzp_pay_failed");
        paymentJson.put("order_id", "rzp_order_failed_pay");
        paymentJson.put("status", "failed");
        paymentJson.put("currency", "INR");
        paymentJson.put("amount", 10000);
        com.razorpay.Payment payment = new com.razorpay.Payment(paymentJson);

        com.razorpay.OrderClient orderClient = mock(com.razorpay.OrderClient.class);
        razorpayClient.orders = orderClient;
        when(orderClient.fetchPayments("rzp_order_failed_pay")).thenReturn(java.util.List.of(payment));

        com.Siddhant.UserApp.dto.SyncPaymentResponse res = paymentService.syncPaymentStatus(order.getOrderId());

        assertFalse(res.isPaid());
        assertEquals(PaymentStatus.PENDING, res.getPaymentStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus()); // Must remain PENDING, NEVER marked FAILED
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testSyncPaymentStatus_AnotherUserOrder_RejectedWith403() {
        User loggedInUser = new User();
        loggedInUser.setUserId(UUID.randomUUID());
        loggedInUser.setEmail("me@test.com");

        User otherUser = new User();
        otherUser.setUserId(UUID.randomUUID());
        otherUser.setEmail("other@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(otherUser);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId("rzp_order_other");

        mockSecurityContext(loggedInUser);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                paymentService.syncPaymentStatus(order.getOrderId()));
        assertEquals(org.springframework.http.HttpStatus.FORBIDDEN, ex.getStatusCode());
        verifyNoInteractions(razorpayClient);
    }

    @Test
    void testSyncPaymentStatus_RazorpayException_MapsTo502WithoutChangingOrder() throws Exception {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomer(user);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setRazorpayOrderId("rzp_order_err");
        order.setTotalAmount(new BigDecimal("100.00"));

        mockSecurityContext(user);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        com.razorpay.OrderClient orderClient = mock(com.razorpay.OrderClient.class);
        razorpayClient.orders = orderClient;
        when(orderClient.fetchPayments("rzp_order_err")).thenThrow(new RazorpayException("Razorpay server error"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                paymentService.syncPaymentStatus(order.getOrderId()));
        assertEquals(org.springframework.http.HttpStatus.BAD_GATEWAY, ex.getStatusCode());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        verify(orderRepository, never()).save(any());
    }
}
