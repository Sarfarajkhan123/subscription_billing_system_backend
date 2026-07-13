package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.Meter;
import com.subscript.subscription.api.model.PlanMeterMapping;
import org.springframework.http.ResponseEntity;

public interface AdminMeterController {

    ResponseEntity<Meter> createMeter(Meter meter);

    ResponseEntity<PlanMeterMapping> createPlanMeterMapping(PlanMeterMapping mapping);
}
