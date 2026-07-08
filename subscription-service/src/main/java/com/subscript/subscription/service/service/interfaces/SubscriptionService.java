package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.Subscription;
import java.util.List;

public interface SubscriptionService {
    Subscription subscribe(Integer customerId, Integer planId);
    void checkTrialStatus(Integer subscriptionId);
    List<Subscription> getAllSubscriptions();
    List<Subscription> getSubscriptionsByCustomer(Integer customerId);
    Subscription getSubscriptionById(Integer id);
    Subscription upgradePlan(Integer subscriptionId, Integer newPlanId);
    Subscription cancelSubscription(Integer id);
    void deleteSubscription(Integer id);
}
