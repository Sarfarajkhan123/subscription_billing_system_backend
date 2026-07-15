package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.response.SubscriptionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubscriptionController {

        ResponseEntity<SubscriptionResponse> subscribe(
                        Integer customerId,
                        Integer planId,
                        String couponCode);

        ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions();

        ResponseEntity<List<SubscriptionResponse>> getByCustomer(
                        Integer customerId);

        ResponseEntity<SubscriptionResponse> getById(
                        Integer id);

        ResponseEntity<SubscriptionResponse> upgradePlan(
                        Integer id,
                        Integer newPlanId);

        ResponseEntity<SubscriptionResponse> cancelSubscription(
                        Integer id);

        ResponseEntity<String> deleteSubscription(
                        Integer id);

        ResponseEntity<?> getTrialStatus(Integer id);

}