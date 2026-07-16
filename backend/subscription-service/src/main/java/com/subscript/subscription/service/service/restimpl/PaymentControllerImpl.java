package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.PaymentController;
import com.subscript.subscription.api.wrapper.request.PaymentOrderRequest;
import com.subscript.subscription.api.wrapper.request.PaymentRequest;
import com.subscript.subscription.api.wrapper.request.PaymentVerifyRequest;
import com.subscript.subscription.api.wrapper.response.PaymentOrderResponse;
import com.subscript.subscription.api.wrapper.response.PaymentResponse;
import com.subscript.subscription.service.service.interfaces.CustomerService;
import com.subscript.subscription.service.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentControllerImpl implements PaymentController {

    private final PaymentService paymentService;
    private final CustomerService customerService;

    // POST /api/payments/process
    @Override
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    // POST /api/payments/order — start Razorpay checkout for the caller's invoice.
    @Override
    @PostMapping("/order")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @RequestBody PaymentOrderRequest request) {
        Integer customerId = currentCustomerId();
        return ResponseEntity.ok(
                paymentService.createRazorpayOrder(request.getInvoiceId(), customerId));
    }

    // POST /api/payments/verify — verify Razorpay signature and finalize.
    @Override
    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @RequestBody PaymentVerifyRequest request) {
        return ResponseEntity.ok(
                paymentService.verifyRazorpayPayment(request));
    }

    // GET /api/payments
    @Override
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments());
    }

    // GET /api/payments/customer/{customerId}
    @Override
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentResponse>> getByCustomer(
            @PathVariable Integer customerId) {

        enforceOwnership(customerId);

        return ResponseEntity.ok(
                paymentService.getPaymentsByCustomer(customerId));
    }

    // GET /api/payments/invoice/{invoiceId}
    @Override
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentResponse>> getByInvoice(
            @PathVariable Integer invoiceId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByInvoice(invoiceId));
    }

    // GET /api/payments/{id}
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(id));
    }

    // DELETE /api/payments/{id}
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(
            @PathVariable Integer id) {

        paymentService.deletePayment(id);

        return ResponseEntity.ok("Payment deleted successfully.");
    }

    /** Resolve the signed-in customer's id from the JWT (for own-invoice payment). */
    private Integer currentCustomerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            return customerService.getMyProfile(auth.getName()).getCustomerId();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only customers can start a payment.");
        }
    }

    /**
     * A CUSTOMER may only read their OWN payment history; Finance / IT Admin are
     * privileged and unrestricted. Identity comes from the JWT.
     */
    private void enforceOwnership(Integer customerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean privileged = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FINANCE")
                        || a.getAuthority().equals("ROLE_IT_ADMIN"));
        if (privileged) {
            return;
        }
        Integer ownId;
        try {
            ownId = customerService.getMyProfile(auth.getName()).getCustomerId();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only access your own payments.");
        }
        if (!ownId.equals(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only access your own payments.");
        }
    }
}