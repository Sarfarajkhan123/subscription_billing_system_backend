package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.UsageData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsageDataRepository extends JpaRepository<UsageData, Integer> {
    List<UsageData> findBySubscription_SubscriptionId(Integer subscriptionId);

    Optional<UsageData> findTopBySubscription_SubscriptionIdOrderByRecordedAtDesc(Integer subscriptionId);
}