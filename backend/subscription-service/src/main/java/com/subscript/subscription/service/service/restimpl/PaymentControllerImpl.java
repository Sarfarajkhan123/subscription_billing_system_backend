package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.PaymentController;
import com.subscript.subscription.api.wrapper.request.PaymentRequest;
import com.subscript.subscription.api.wrapper.response.PaymentResponse;
import com.subscript.subscription.service.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentControllerImpl implements PaymentController {

    private final PaymentService paymentService;

    // POST /api/payments/process
    @Override
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
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


}