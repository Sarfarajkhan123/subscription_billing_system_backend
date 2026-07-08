package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
    List<SubscriptionPlan> findByService_ServiceIdAndIsActiveTrue(Integer serviceId);
}
