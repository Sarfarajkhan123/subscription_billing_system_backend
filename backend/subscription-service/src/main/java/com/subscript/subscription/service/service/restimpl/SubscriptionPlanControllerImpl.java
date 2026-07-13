package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.SubscriptionPlanController;
import com.subscript.subscription.api.model.SubscriptionPlan;
import com.subscript.subscription.service.service.interfaces.SubscriptionPlanService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriptionPlanControllerImpl implements SubscriptionPlanController {

        private final SubscriptionPlanService planService;

        // POST /api/plans?serviceId=1
        @Override
        @PostMapping
        public ResponseEntity<SubscriptionPlan> createPlan(
                        @RequestParam Integer serviceId,
                        @RequestBody SubscriptionPlan plan) {

                System.out.println("Controller reached");

                return ResponseEntity.ok(
                                planService.createPlan(serviceId, plan));
        }

        // GET /api/plans
        @Override
        @GetMapping
        public ResponseEntity<List<SubscriptionPlan>> getAllPlans() {

                return ResponseEntity.ok(
                                planService.getAllPlans());
        }

        // GET /api/plans/service/{serviceId}
        @Override
        @GetMapping("/service/{serviceId}")
        public ResponseEntity<List<SubscriptionPlan>> getPlansByService(
                        @PathVariable Integer serviceId) {

                return ResponseEntity.ok(
                                planService.getPlansByService(serviceId));
        }

        // GET /api/plans/{id}
        @Override
        @GetMapping("/{id}")
        public ResponseEntity<SubscriptionPlan> getPlanById(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                planService.getPlanById(id));
        }

        // PUT /api/plans/{id}
        @Override
        @PutMapping("/{id}")
        public ResponseEntity<SubscriptionPlan> updatePlan(
                        @PathVariable Integer id,
                        @RequestBody SubscriptionPlan updated) {

                return ResponseEntity.ok(
                                planService.updatePlan(id, updated));
        }

        // DELETE /api/plans/{id}
        @Override
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deletePlan(
                        @PathVariable Integer id) {

                planService.deletePlan(id);

                return ResponseEntity.ok("Plan deleted");
        }
}