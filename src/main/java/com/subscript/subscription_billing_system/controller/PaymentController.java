package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.Payment;
import com.subscript.subscription_billing_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final PaymentService paymentService;

    // POST /api/payments/process?invoiceId=1&customerId=1&paymentMethod=mock_card
    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(
            @RequestParam Integer invoiceId,
            @RequestParam Integer customerId,
            @RequestParam(defaultValue = "mock_card") String paymentMethod) {
        return ResponseEntity.ok(
                paymentService.processPayment(invoiceId, customerId, paymentMethod));
    }

    // GET /api/payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // GET /api/payments/customer/1
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerId));
    }

    // GET /api/payments/invoice/1
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<Payment>> getByInvoice(@PathVariable Integer invoiceId) {
        return ResponseEntity.ok(paymentService.getPaymentsByInvoice(invoiceId));
    }

    // GET /api/payments/1
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // DELETE /api/payments/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Integer id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok("Payment deleted");
    }
}