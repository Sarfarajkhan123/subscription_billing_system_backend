package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.SaasService;
import com.subscript.subscription_billing_system.entity.SubscriptionPlan;
import com.subscript.subscription_billing_system.repository.SaasServiceRepository;
import com.subscript.subscription_billing_system.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

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