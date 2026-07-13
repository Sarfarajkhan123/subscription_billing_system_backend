package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.SaasService;
import com.subscript.subscription.api.model.SubscriptionPlan;
import com.subscript.subscription.service.repository.SaasServiceRepository;
import com.subscript.subscription.service.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlan createPlan(Integer serviceId, SubscriptionPlan plan);
    List<SubscriptionPlan> getAllPlans();
    List<SubscriptionPlan> getPlansByService(Integer serviceId);
    SubscriptionPlan getPlanById(Integer id);
    SubscriptionPlan updatePlan(Integer id, SubscriptionPlan updated);
    void deletePlan(Integer id);
}
