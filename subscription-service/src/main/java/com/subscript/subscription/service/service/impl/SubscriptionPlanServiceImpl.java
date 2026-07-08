package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.service.service.interfaces.SubscriptionPlanService;

import com.subscript.subscription.api.model.SaasService;
import com.subscript.subscription.api.model.SubscriptionPlan;
import com.subscript.subscription.service.repository.SaasServiceRepository;
import com.subscript.subscription.service.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SaasServiceRepository saasServiceRepository;

    public SubscriptionPlan createPlan(Integer serviceId, SubscriptionPlan plan) {
        SaasService service = saasServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found: " + serviceId));
        plan.setService(service);
        return planRepository.save(plan);
    }

    public List<SubscriptionPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public List<SubscriptionPlan> getPlansByService(Integer serviceId) {
        return planRepository.findByService_ServiceIdAndIsActiveTrue(serviceId);
    }

    public SubscriptionPlan getPlanById(Integer id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));
    }

    public SubscriptionPlan updatePlan(Integer id, SubscriptionPlan updated) {
        SubscriptionPlan existing = getPlanById(id);
        if (updated.getPlanName() != null)
            existing.setPlanName(updated.getPlanName());
        if (updated.getBasePrice() != null)
            existing.setBasePrice(updated.getBasePrice());
        if (updated.getUsageLimit() != null)
            existing.setUsageLimit(updated.getUsageLimit());
        if (updated.getFeatures() != null)
            existing.setFeatures(updated.getFeatures());
        if (updated.getIsActive() != null)
            existing.setIsActive(updated.getIsActive());
        if (updated.getBillingCycle() != null)
            existing.setBillingCycle(updated.getBillingCycle());
        return planRepository.save(existing);
    }

    public void deletePlan(Integer id) {
        getPlanById(id);
        planRepository.deleteById(id);
    }
}