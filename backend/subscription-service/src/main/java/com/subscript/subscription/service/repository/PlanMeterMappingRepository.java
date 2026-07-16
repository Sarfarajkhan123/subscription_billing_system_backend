package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.PlanMeterMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanMeterMappingRepository extends JpaRepository<PlanMeterMapping, Integer> {
    List<PlanMeterMapping> findByPlan_PlanId(Integer planId);
    List<PlanMeterMapping> findByMeter_MeterId(Integer meterId);
    java.util.Optional<PlanMeterMapping> findFirstByPlan_PlanId(Integer planId);
}
