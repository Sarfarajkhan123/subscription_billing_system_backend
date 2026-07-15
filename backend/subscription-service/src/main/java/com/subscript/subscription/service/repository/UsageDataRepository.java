package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.UsageData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsageDataRepository extends JpaRepository<UsageData, Integer> {
    List<UsageData> findBySubscription_SubscriptionId(Integer subscriptionId);

    Optional<UsageData> findTopBySubscription_SubscriptionIdOrderByRecordedAtDesc(Integer subscriptionId);

    void deleteBySubscription_SubscriptionId(Integer subscriptionId);
}