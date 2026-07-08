package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.SubscriptionController;
import com.subscript.subscription.api.model.PlanMeterMapping;
import com.subscript.subscription.api.model.Subscription;
import com.subscript.subscription.api.model.UsageData;
import com.subscript.subscription.service.repository.PlanMeterMappingRepository;
import com.subscript.subscription.service.repository.UsageDataRepository;
import com.subscript.subscription.service.service.interfaces.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriptionControllerImpl implements SubscriptionController {

        private final SubscriptionService subscriptionService;
        private final UsageDataRepository usageDataRepository;
        private final PlanMeterMappingRepository planMeterMappingRepository;

        // POST /api/subscriptions?customerId=1&planId=2
        @Override
        @PostMapping
        public ResponseEntity<Subscription> subscribe(
                        @RequestParam Integer customerId,
                        @RequestParam Integer planId) {
                return ResponseEntity.ok(subscriptionService.subscribe(customerId, planId));
        }

        // GET /api/subscriptions
        @Override
        @GetMapping
        public ResponseEntity<List<Subscription>> getAllSubscriptions() {
                return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
        }

        // GET /api/subscriptions/customer/{customerId}
        @Override
        @GetMapping("/customer/{customerId}")
        public ResponseEntity<List<Subscription>> getByCustomer(
                        @PathVariable Integer customerId) {
                return ResponseEntity.ok(subscriptionService.getSubscriptionsByCustomer(customerId));
        }

        // GET /api/subscriptions/{id}
        @Override
        @GetMapping("/{id}")
        public ResponseEntity<Subscription> getById(
                        @PathVariable Integer id) {
                return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
        }

        // PUT /api/subscriptions/{id}/upgrade?newPlanId=3
        @Override
        @PutMapping("/{id}/upgrade")
        public ResponseEntity<Subscription> upgradePlan(
                        @PathVariable Integer id,
                        @RequestParam Integer newPlanId) {
                return ResponseEntity.ok(subscriptionService.upgradePlan(id, newPlanId));
        }

        // PUT /api/subscriptions/{id}/cancel
        @Override
        @PutMapping("/{id}/cancel")
        public ResponseEntity<Subscription> cancelSubscription(
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
         *          usageConsumed (per meter), usageLimit (per meter), isTrialActive
         */
        @Override
        @GetMapping("/{id}/trial-status")
        public ResponseEntity<?> getTrialStatus(@PathVariable Integer id) {
                Subscription sub = subscriptionService.getSubscriptionById(id);

                long daysRemaining = 0;
                if (sub.getTrialEndDate() != null) {
                        daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), sub.getTrialEndDate());
                        if (daysRemaining < 0) daysRemaining = 0;
                }

                boolean isTrialActive = sub.getStatus() == Subscription.Status.trial;

                // Collect per-meter usage summary
                List<UsageData> usageList = usageDataRepository
                        .findBySubscription_SubscriptionId(id);
                List<PlanMeterMapping> mappings = planMeterMappingRepository
                        .findByPlan_PlanId(sub.getPlan().getPlanId());

                List<Map<String, Object>> meterStats = mappings.stream().map(m -> {
                        int consumed = usageList.stream()
                                .filter(u -> u.getMeter() != null
                                        && u.getMeter().getMeterId().equals(m.getMeter().getMeterId())
                                        && u.getApiCalls() != null)
                                .mapToInt(UsageData::getApiCalls)
                                .sum();
                        Map<String, Object> stat = new LinkedHashMap<>();
                        stat.put("meterId", m.getMeter().getMeterId());
                        stat.put("meterName", m.getMeter().getName());
                        stat.put("unit", m.getMeter().getUnit());
                        stat.put("usageConsumed", consumed);
                        stat.put("freeUnits", m.getFreeUnits());
                        stat.put("maxLimit", m.getMaxLimit());
                        return stat;
                }).toList();

                Map<String, Object> response = new LinkedHashMap<>();
                response.put("subscriptionId", sub.getSubscriptionId());
                response.put("status", sub.getStatus());
                response.put("trialEndDate", sub.getTrialEndDate() != null ? sub.getTrialEndDate().toString() : null);
                response.put("daysRemaining", daysRemaining);
                response.put("isTrialActive", isTrialActive);
                response.put("meterUsage", meterStats);

                return ResponseEntity.ok(response);
        }
}