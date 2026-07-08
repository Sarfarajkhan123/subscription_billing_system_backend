package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.Subscription;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubscriptionController {

        ResponseEntity<Subscription> subscribe(
                        Integer customerId,
                        Integer planId);

        ResponseEntity<List<Subscription>> getAllSubscriptions();

        ResponseEntity<List<Subscription>> getByCustomer(
                        Integer customerId);

        ResponseEntity<Subscription> getById(
                        Integer id);

        ResponseEntity<Subscription> upgradePlan(
                        Integer id,
                        Integer newPlanId);

        ResponseEntity<Subscription> cancelSubscription(
                        Integer id);

        ResponseEntity<String> deleteSubscription(
                        Integer id);

        ResponseEntity<?> getTrialStatus(Integer id);

}