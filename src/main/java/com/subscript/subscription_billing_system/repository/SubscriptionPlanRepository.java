package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
    List<SubscriptionPlan> findByService_ServiceIdAndIsActiveTrue(Integer serviceId);
}
