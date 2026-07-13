package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.UsageData;
import com.subscript.subscription.api.wrapper.request.UsageDataRequest;
import com.subscript.subscription.api.wrapper.response.UsageDataResponse;

import java.math.BigDecimal;
import java.util.List;

public interface UsageDataService {

    // Record usage
    UsageDataResponse recordUsage(UsageDataRequest request);

    // Get all usage records for a subscription
    List<UsageDataResponse> getUsageBySubscription(Integer subscriptionId);

    // Get latest usage record
    UsageDataResponse getLatestUsage(Integer subscriptionId);

    // Get usage by id
    UsageDataResponse getUsageById(Integer id);

    // Delete usage record
    void deleteUsage(Integer id);

    UsageData recordUsage(Integer subscriptionId, Integer meterId, Integer apiCalls, Integer activeUsers,
            BigDecimal storageGb);
}