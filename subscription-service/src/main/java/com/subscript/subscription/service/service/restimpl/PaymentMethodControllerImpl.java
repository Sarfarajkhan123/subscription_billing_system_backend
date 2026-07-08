package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.PaymentMethodController;
import com.subscript.subscription.api.model.PaymentMethod;
import com.subscript.subscription.service.service.interfaces.PaymentMethodService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentMethodControllerImpl implements PaymentMethodController {

        private final PaymentMethodService paymentMethodService;

        // POST /api/payment-methods?customerId=1
        @Override
        @PostMapping
        public ResponseEntity<PaymentMethod> addMethod(
                        @RequestParam Integer customerId,
                        @RequestBody PaymentMethod method) {

                return ResponseEntity.ok(
                                paymentMethodService.addPaymentMethod(customerId, method));
        }

        // GET /api/payment-methods/customer/{customerId}
        @Override
        @GetMapping("/customer/{customerId}")
        public ResponseEntity<List<PaymentMethod>> getByCustomer(
                        @PathVariable Integer customerId) {

                return ResponseEntity.ok(
                                paymentMethodService.getMethodsByCustomer(customerId));
        }

        // GET /api/payment-methods/{id}
        @Override
        @GetMapping("/{id}")
        public ResponseEntity<PaymentMethod> getById(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                paymentMethodService.getMethodById(id));
        }

        // PUT /api/payment-methods/customer/{customerId}/set-default/{methodId}
        @Override
        @PutMapping("/customer/{customerId}/set-default/{methodId}")
        public ResponseEntity<PaymentMethod> setDefault(
                        @PathVariable Integer customerId,
                        @PathVariable Integer methodId) {

                return ResponseEntity.ok(
                                paymentMethodService.setDefault(customerId, methodId));
        }

        // DELETE /api/payment-methods/{id}
        @Override
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteMethod(
                        @PathVariable Integer id) {

                paymentMethodService.deleteMethod(id);

                return ResponseEntity.ok("Payment method deleted");
        }
}