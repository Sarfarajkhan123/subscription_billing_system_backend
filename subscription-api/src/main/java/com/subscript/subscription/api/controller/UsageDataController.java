package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.request.UsageDataRequest;
import com.subscript.subscription.api.wrapper.response.UsageDataResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UsageDataController {

        // Record usage
        ResponseEntity<UsageDataResponse> recordUsage(
                        UsageDataRequest request);

        // Get all usage records for a subscription
        ResponseEntity<List<UsageDataResponse>> getBySubscription(
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
}