package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.SubscriptionPlan;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubscriptionPlanController {

        ResponseEntity<SubscriptionPlan> createPlan(
                        Integer serviceId,
                        SubscriptionPlan plan);

        ResponseEntity<List<SubscriptionPlan>> getAllPlans();

        ResponseEntity<List<SubscriptionPlan>> getPlansByService(
                        Integer serviceId);

        ResponseEntity<SubscriptionPlan> getPlanById(
                        Integer id);

        ResponseEntity<SubscriptionPlan> updatePlan(
                        Integer id,
                        SubscriptionPlan updated);

        ResponseEntity<String> deletePlan(
                        Integer id);

}