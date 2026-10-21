package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Service.PaymentService;
import com.Siddhant.UserApp.dto.CreateOrderRequest;
import com.Siddhant.UserApp.dto.CreateOrderResponse;
import com.Siddhant.UserApp.dto.VerifyPaymentRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = paymentService.createRazorpayOrder(request.getOrderId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@Valid @RequestBody VerifyPaymentRequest request) {
        paymentService.verifyPayment(request.getOrderId(), request.getRazorpay_order_id(), request.getRazorpay_payment_id(), request.getRazorpay_signature());
        return ResponseEntity.ok("Payment verified successfully");
    }

    @PostMapping("/sync")
    public ResponseEntity<com.Siddhant.UserApp.dto.SyncPaymentResponse> syncPayment(@Valid @RequestBody com.Siddhant.UserApp.dto.SyncPaymentRequest request) {
        com.Siddhant.UserApp.dto.SyncPaymentResponse response = paymentService.syncPaymentStatus(request.getOrderId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String rawBody, @RequestHeader("X-Razorpay-Signature") String signatureHeader) {
        paymentService.handleWebhookEvent(rawBody, signatureHeader);
        return ResponseEntity.ok("Webhook processed");
    }
}
