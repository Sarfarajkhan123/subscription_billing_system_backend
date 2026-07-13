package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.AdminMeterController;
import com.subscript.subscription.api.model.Meter;
import com.subscript.subscription.api.model.PlanMeterMapping;
import com.subscript.subscription.service.repository.MeterRepository;
import com.subscript.subscription.service.repository.PlanMeterMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminMeterControllerImpl implements AdminMeterController {

    private final MeterRepository meterRepository;
    private final PlanMeterMappingRepository planMeterMappingRepository;

    // POST /api/admin/meters
    @Override
    @PostMapping("/meters")
    public ResponseEntity<Meter> createMeter(@RequestBody Meter meter) {
        return ResponseEntity.ok(meterRepository.save(meter));
    }

    // POST /api/admin/plan-meter-mapping
    @Override
    @PostMapping("/plan-meter-mapping")
    public ResponseEntity<PlanMeterMapping> createPlanMeterMapping(
            @RequestBody PlanMeterMapping mapping) {
        return ResponseEntity.ok(planMeterMappingRepository.save(mapping));
    }
}
