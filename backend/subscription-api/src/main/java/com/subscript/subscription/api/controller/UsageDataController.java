package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.request.UsageDataRequest;
import com.subscript.subscription.api.wrapper.response.UsageDataResponse;
import com.subscript.subscription.api.wrapper.response.UsageSummaryResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UsageDataController {

        // Record usage
        ResponseEntity<UsageDataResponse> recordUsage(
                        @org.springframework.web.bind.annotation.RequestBody UsageDataRequest request);

        // Get all usage records for a subscription
        ResponseEntity<List<UsageDataResponse>> getBySubscription(
                        Integer subscriptionId);

        // Meter-based usage summary (limit / remaining / % / overage)
        ResponseEntity<UsageSummaryResponse> getUsageSummary(
                        Integer subscriptionId);

        // Get latest usage record
        ResponseEntity<UsageDataResponse> getLatest(
                        Integer subscriptionId);

        // Get usage by id
        ResponseEntity<UsageDataResponse> getById(
                        Integer id);

        // Delete usage record
        ResponseEntity<String> deleteUsage(
                        Integer id);

        // Reset (clear) all usage for a subscription
        ResponseEntity<String> deleteBySubscription(
                        Integer subscriptionId);
}