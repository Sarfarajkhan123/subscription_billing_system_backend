package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.SubscriptionPlan;
import com.subscript.subscription_billing_system.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriptionPlanController {

    private final SubscriptionPlanService planService;

    // POST /api/plans?serviceId=1
    @PostMapping
    public ResponseEntity<SubscriptionPlan> createPlan(
            @RequestParam Integer serviceId,
            @RequestBody SubscriptionPlan plan) {

        System.out.println("Controller reached");

        return ResponseEntity.ok(planService.createPlan(serviceId, plan));
    }

    // GET /api/plans
    @GetMapping
    public ResponseEntity<List<SubscriptionPlan>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    // GET /api/plans/service/1
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<SubscriptionPlan>> getPlansByService(
            @PathVariable Integer serviceId) {
        return ResponseEntity.ok(planService.getPlansByService(serviceId));
    }

    // GET /api/plans/1
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> getPlanById(@PathVariable Integer id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    // PUT /api/plans/1
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> updatePlan(
            @PathVariable Integer id,
            @RequestBody SubscriptionPlan updated) {
        return ResponseEntity.ok(planService.updatePlan(id, updated));
    }

    // DELETE /api/plans/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlan(@PathVariable Integer id) {
        planService.deletePlan(id);
        return ResponseEntity.ok("Plan deleted");
    }
}