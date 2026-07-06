package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.PaymentMethod;
import com.subscript.subscription_billing_system.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    // POST /api/payment-methods?customerId=1
    @PostMapping
    public ResponseEntity<PaymentMethod> addMethod(
            @RequestParam Integer customerId,
            @RequestBody PaymentMethod method) {
        return ResponseEntity.ok(paymentMethodService.addPaymentMethod(customerId, method));
    }

    // GET /api/payment-methods/customer/1
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentMethod>> getByCustomer(
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(paymentMethodService.getMethodsByCustomer(customerId));
    }

    // GET /api/payment-methods/1
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentMethodService.getMethodById(id));
    }

    // PUT /api/payment-methods/customer/1/set-default/2
    @PutMapping("/customer/{customerId}/set-default/{methodId}")
    public ResponseEntity<PaymentMethod> setDefault(
            @PathVariable Integer customerId,
            @PathVariable Integer methodId) {
        return ResponseEntity.ok(paymentMethodService.setDefault(customerId, methodId));
    }

    // DELETE /api/payment-methods/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMethod(@PathVariable Integer id) {
        paymentMethodService.deleteMethod(id);
        return ResponseEntity.ok("Payment method deleted");
    }
}
