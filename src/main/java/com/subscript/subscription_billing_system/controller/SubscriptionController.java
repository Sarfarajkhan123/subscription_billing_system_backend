package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.Subscription;
import com.subscript.subscription_billing_system.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // POST /api/subscriptions?customerId=1&planId=2
    @PostMapping
    public ResponseEntity<Subscription> subscribe(
            @RequestParam Integer customerId,
            @RequestParam Integer planId) {
        return ResponseEntity.ok(subscriptionService.subscribe(customerId, planId));
    }

    // GET /api/subscriptions
    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    // GET /api/subscriptions/customer/1
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Subscription>> getByCustomer(
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByCustomer(customerId));
    }

    // GET /api/subscriptions/1
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    // PUT /api/subscriptions/1/upgrade?newPlanId=3
    @PutMapping("/{id}/upgrade")
    public ResponseEntity<Subscription> upgradePlan(
            @PathVariable Integer id,
            @RequestParam Integer newPlanId) {
        return ResponseEntity.ok(subscriptionService.upgradePlan(id, newPlanId));
    }

    // PUT /api/subscriptions/1/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Subscription> cancelSubscription(@PathVariable Integer id) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
    }

    // DELETE /api/subscriptions/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubscription(@PathVariable Integer id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok("Subscription deleted");
    }
}