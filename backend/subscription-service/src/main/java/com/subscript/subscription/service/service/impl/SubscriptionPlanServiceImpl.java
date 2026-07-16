package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.service.service.interfaces.SubscriptionPlanService;

import com.subscript.subscription.api.model.Meter;
import com.subscript.subscription.api.model.PlanMeterMapping;
import com.subscript.subscription.api.model.SaasService;
import com.subscript.subscription.api.model.SubscriptionPlan;
import com.subscript.subscription.service.repository.MeterRepository;
import com.subscript.subscription.service.repository.PlanMeterMappingRepository;
import com.subscript.subscription.service.repository.SaasServiceRepository;
import com.subscript.subscription.service.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SaasServiceRepository saasServiceRepository;
    private final MeterRepository meterRepository;
    private final PlanMeterMappingRepository planMeterMappingRepository;

    /**
     * Create a plan and automatically provision its PlanMeterMapping.
     *
     * The caller may include meter config in the transient fields of the
     * SubscriptionPlan body (freeUnits, maxLimit, pricePerUnit).  If the
     * service has no meter yet one is created as a fallback (ensures the
     * system always has a meter even for services created before this
     * auto-provisioning was introduced).
     */
    @Override
    @Transactional
    public SubscriptionPlan createPlan(Integer serviceId, SubscriptionPlan plan) {

        SaasService service = saasServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found: " + serviceId));
        plan.setService(service);

        SubscriptionPlan saved = planRepository.save(plan);

        // ── Auto-provision PlanMeterMapping ───────────────────────────────
        // Find (or lazily create) the service's default meter.
        Meter meter = getOrCreateDefaultMeter(service);

        // Only create a mapping if none exists for this plan yet (duplicate-safe).
        List<PlanMeterMapping> existingMappings =
                planMeterMappingRepository.findByPlan_PlanId(saved.getPlanId());

        if (existingMappings.isEmpty()) {
            PlanMeterMapping mapping = new PlanMeterMapping();
            mapping.setPlan(saved);
            mapping.setMeter(meter);
            mapping.setFreeUnits(plan.getFreeUnits() != null ? plan.getFreeUnits() : 0);
            mapping.setMaxLimit(plan.getMaxLimit() != null
                    ? plan.getMaxLimit()
                    : (saved.getUsageLimit() != null ? saved.getUsageLimit() : null));
            mapping.setPricePerUnit(plan.getPricePerUnit() != null
                    ? plan.getPricePerUnit()
                    : BigDecimal.ZERO);
            planMeterMappingRepository.save(mapping);
        }

        return saved;
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

    /**
     * Update plan fields, and update the existing PlanMeterMapping if meter
     * config fields are present in the request.  Never creates a second mapping.
     */
    @Override
    @Transactional
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

        SubscriptionPlan saved = planRepository.save(existing);

        // ── Update PlanMeterMapping if meter config is present ────────────
        boolean hasMeterUpdate = updated.getFreeUnits() != null
                || updated.getMaxLimit() != null
                || updated.getPricePerUnit() != null;

        if (hasMeterUpdate) {
            Optional<PlanMeterMapping> optMapping =
                    planMeterMappingRepository.findFirstByPlan_PlanId(id);

            if (optMapping.isPresent()) {
                // Update existing mapping — do not create a second one.
                PlanMeterMapping mapping = optMapping.get();
                if (updated.getFreeUnits() != null)
                    mapping.setFreeUnits(updated.getFreeUnits());
                if (updated.getMaxLimit() != null)
                    mapping.setMaxLimit(updated.getMaxLimit());
                if (updated.getPricePerUnit() != null)
                    mapping.setPricePerUnit(updated.getPricePerUnit());
                planMeterMappingRepository.save(mapping);
            } else {
                // No mapping yet (plan pre-dates auto-provisioning) — create one now.
                Meter meter = getOrCreateDefaultMeter(existing.getService());
                PlanMeterMapping mapping = new PlanMeterMapping();
                mapping.setPlan(saved);
                mapping.setMeter(meter);
                mapping.setFreeUnits(updated.getFreeUnits() != null ? updated.getFreeUnits() : 0);
                mapping.setMaxLimit(updated.getMaxLimit());
                mapping.setPricePerUnit(updated.getPricePerUnit() != null
                        ? updated.getPricePerUnit()
                        : BigDecimal.ZERO);
                planMeterMappingRepository.save(mapping);
            }
        }

        return saved;
    }

    /**
     * Delete a plan. Removes all PlanMeterMappings for the plan first.
     * The Meter itself is NOT deleted (other plans may reference it).
     */
    @Override
    @Transactional
    public void deletePlan(Integer id) {
        getPlanById(id); // ensures it exists
        // Remove mappings before deleting plan to avoid FK violations.
        List<PlanMeterMapping> mappings = planMeterMappingRepository.findByPlan_PlanId(id);
        planMeterMappingRepository.deleteAll(mappings);
        planRepository.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /**
     * Returns the first existing meter for the service, or creates a default
     * one if none exists (fallback for services created before auto-provisioning).
     */
    private Meter getOrCreateDefaultMeter(SaasService service) {
        List<Meter> meters = meterRepository.findByService_ServiceId(service.getServiceId());
        if (!meters.isEmpty()) {
            return meters.get(0);
        }
        // Lazy creation — should normally have been created by SaasServiceServiceImpl.
        Meter meter = new Meter();
        meter.setService(service);
        meter.setName(service.getName() + " API Calls");
        meter.setUnit("api_calls");
        return meterRepository.save(meter);
    }
}