package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.Entity.Order;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.dto.CreateOrderResponse;
import com.razorpay.RazorpayException;
import java.util.UUID;
public interface PaymentService {
    CreateOrderResponse createRazorpayOrder(UUID orderId);
    void verifyPayment(UUID orderId, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature);
    void handleWebhookEvent(String rawBody, String signatureHeader);
    com.Siddhant.UserApp.dto.SyncPaymentResponse syncPaymentStatus(UUID orderId);

    /**
     * Internal-use sync: checks Razorpay for a captured payment on the given order.
     * Does NOT require a logged-in user. Intended for the cleanup scheduler.
     * Throws RazorpayException on Razorpay API failure so the caller can skip
     * cancellation instead of wrongly cancelling a possibly-paid order.
     */
    com.Siddhant.UserApp.dto.SyncPaymentResponse syncPaymentStatusForScheduler(Order order) throws RazorpayException;
}
