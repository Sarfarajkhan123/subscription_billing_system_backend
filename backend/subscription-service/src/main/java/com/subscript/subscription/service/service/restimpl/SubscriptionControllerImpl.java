package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.SubscriptionController;
import com.subscript.subscription.api.wrapper.response.SubscriptionResponse;
import com.subscript.subscription.service.service.interfaces.CustomerService;
import com.subscript.subscription.service.service.interfaces.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriptionControllerImpl implements SubscriptionController {

        private final SubscriptionService subscriptionService;
        private final CustomerService customerService;

        // POST /api/subscriptions?customerId=1&planId=2&couponCode=SAVE20
        @Override
        @PostMapping
        public ResponseEntity<SubscriptionResponse> subscribe(
                        @RequestParam Integer customerId,
                        @RequestParam Integer planId,
                        @RequestParam(required = false) String couponCode) {
                enforceOwnership(customerId);
                return ResponseEntity.ok(
                                subscriptionService.subscribe(customerId, planId, couponCode));
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
                enforceOwnership(customerId);
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

        /**
         * Ownership guard: a CUSTOMER may only create/view their OWN subscriptions.
         * Support and IT Admin are privileged and keep full access (unchanged), so
         * there is no regression for those roles. Identity comes from the JWT via
         * the Spring Security context; the caller-supplied customerId is validated
         * against the authenticated user's own customerId.
         */
        private void enforceOwnership(Integer customerId) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                boolean privileged = auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_IT_ADMIN")
                                                || a.getAuthority().equals("ROLE_SUPPORT"));
                if (privileged) {
                        return;
                }

                Integer ownCustomerId;
                try {
                        ownCustomerId = customerService.getMyProfile(auth.getName())
                                        .getCustomerId();
                } catch (RuntimeException ex) {
                        // Caller has no customer profile (e.g. product/finance employee)
                        // — it is simply not their resource. Return 403, not 500.
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "You can only access your own subscriptions.");
                }
                if (!ownCustomerId.equals(customerId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "You can only access your own subscriptions.");
                }
        }
}