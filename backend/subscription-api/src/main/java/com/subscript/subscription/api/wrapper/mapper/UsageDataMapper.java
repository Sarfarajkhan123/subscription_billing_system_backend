package com.subscript.subscription.api.wrapper.mapper;

import com.subscript.subscription.api.model.UsageData;
import com.subscript.subscription.api.wrapper.response.UsageDataResponse;

public class UsageDataMapper {

    private UsageDataMapper() {}

    public static UsageDataResponse toResponse(UsageData usage) {
        if (usage == null) {
            return null;
        }

        UsageDataResponse response = new UsageDataResponse();
        response.setUsageId(usage.getUsageId());

        if (usage.getSubscription() != null) {
            response.setSubscriptionId(usage.getSubscription().getSubscriptionId());
        }
        if (usage.getSaasService() != null) {
            response.setSaasServiceId(usage.getSaasService().getServiceId());
        }
        if (usage.getMeter() != null) {
            response.setMeterId(usage.getMeter().getMeterId());
        }

        response.setPeriodStart(usage.getPeriodStart());
        response.setPeriodEnd(usage.getPeriodEnd());
        response.setValue(usage.getValue());
        response.setUnit(usage.getUnit());
        response.setApiCalls(usage.getApiCalls());
        response.setActiveUsers(usage.getActiveUsers());
        response.setStorageGb(usage.getStorageGb());
        response.setOverageCharge(usage.getOverageCharge());
        response.setRecordedAt(usage.getRecordedAt());

        return response;
    }
}
