package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.SubscriptionController;
import com.subscript.subscription.api.wrapper.response.SubscriptionResponse;
import com.subscript.subscription.service.service.interfaces.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriptionControllerImpl implements SubscriptionController {

        private final SubscriptionService subscriptionService;

        // POST /api/subscriptions?customerId=1&planId=2
        @Override
        @PostMapping
        public ResponseEntity<SubscriptionResponse> subscribe(
                        @RequestParam Integer customerId,
                        @RequestParam Integer planId) {
                return ResponseEntity.ok(subscriptionService.subscribe(customerId, planId));
        }

        // GET /api/subscriptions
        @Override
        @GetMapping
        public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {
                return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
        }

        // GET /api/subscriptions/customer/{customerId}
        @Override
        @GetMapping("/customer/{customerId}")
        public ResponseEntity<List<SubscriptionResponse>> getByCustomer(
                        @PathVariable Integer customerId) {
                return ResponseEntity.ok(subscriptionService.getSubscriptionsByCustomer(customerId));
        }

        // GET /api/subscriptions/{id}
        @Override
        @GetMapping("/{id}")
        public ResponseEntity<SubscriptionResponse> getById(
                        @PathVariable Integer id) {
                return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
        }

        // PUT /api/subscriptions/{id}/upgrade?newPlanId=3
        @Override
        @PutMapping("/{id}/upgrade")
        public ResponseEntity<SubscriptionResponse> upgradePlan(
                        @PathVariable Integer id,
                        @RequestParam Integer newPlanId) {
                return ResponseEntity.ok(subscriptionService.upgradePlan(id, newPlanId));
        }

        // PUT /api/subscriptions/{id}/cancel
        @Override
        @PutMapping("/{id}/cancel")
        public ResponseEntity<SubscriptionResponse> cancelSubscription(
                        @PathVariable Integer id) {
                return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
        }

        // DELETE /api/subscriptions/{id}
        @Override
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteSubscription(
                        @PathVariable Integer id) {
                subscriptionService.deleteSubscription(id);
                return ResponseEntity.ok("Subscription deleted");
        }

        /**
         * GET /api/subscriptions/{id}/trial-status
         * Returns: subscriptionId, status, trialEndDate, daysRemaining,
         * usageConsumed (per meter), usageLimit (per meter), isTrialActive
         */
        @Override
        @GetMapping("/{id}/trial-status")
        public ResponseEntity<?> getTrialStatus(@PathVariable Integer id) {
                return ResponseEntity.ok(subscriptionService.getTrialStatus(id));
        }
}