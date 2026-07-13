package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.wrapper.response.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {

    SubscriptionResponse subscribe(
            Integer customerId,
            Integer planId);

    List<SubscriptionResponse> getAllSubscriptions();

    List<SubscriptionResponse> getSubscriptionsByCustomer(
            Integer customerId);

    SubscriptionResponse getSubscriptionById(
            Integer id);

    SubscriptionResponse upgradePlan(
            Integer subscriptionId,
            Integer newPlanId);

    SubscriptionResponse cancelSubscription(
            Integer id);

    void deleteSubscription(
            Integer id);

    void checkTrialStatus(
            Integer subscriptionId);

    java.util.Map<String, Object> getTrialStatus(
            Integer subscriptionId);
}
