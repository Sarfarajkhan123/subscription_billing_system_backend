package com.subscript.subscription.api.wrapper.mapper;

import com.subscript.subscription.api.model.Subscription;
import com.subscript.subscription.api.wrapper.response.SubscriptionResponse;

public class SubscriptionMapper {

    private SubscriptionMapper() {
    }

    public static SubscriptionResponse toResponse(Subscription subscription) {

        if (subscription == null) {
            return null;
        }

        SubscriptionResponse response = new SubscriptionResponse();

        response.setSubscriptionId(subscription.getSubscriptionId());

        response.setCustomerId(
                subscription.getCustomer() != null
                        ? subscription.getCustomer().getCustomerId()
                        : null);

        response.setServiceId(
                subscription.getService() != null
                        ? subscription.getService().getServiceId()
                        : null);

        response.setPlanId(
                subscription.getPlan() != null
                        ? subscription.getPlan().getPlanId()
                        : null);

        response.setServiceName(
                subscription.getService() != null
                        ? subscription.getService().getName()
                        : null);

        response.setPlanName(
                subscription.getPlan() != null
                        ? subscription.getPlan().getPlanName()
                        : null);

        response.setBasePrice(
                subscription.getPlan() != null
                        ? subscription.getPlan().getBasePrice()
                        : null);

        response.setBillingCycle(
                subscription.getPlan() != null && subscription.getPlan().getBillingCycle() != null
                        ? subscription.getPlan().getBillingCycle().name()
                        : null);

        response.setStatus(
                subscription.getStatus() != null
                        ? subscription.getStatus().name()
                        : null);

        response.setStartDate(subscription.getStartDate());

        response.setTrialEndDate(subscription.getTrialEndDate());

        response.setRenewalDate(subscription.getRenewalDate());

        response.setApiKey(subscription.getApiKey());

        return response;
    }
}
