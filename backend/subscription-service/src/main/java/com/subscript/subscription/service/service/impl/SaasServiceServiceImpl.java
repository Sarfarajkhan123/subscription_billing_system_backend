package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.service.service.interfaces.SaasServiceService;

import com.subscript.subscription.api.model.Meter;
import com.subscript.subscription.api.model.SaasService;
import com.subscript.subscription.service.repository.MeterRepository;
import com.subscript.subscription.service.repository.PlanMeterMappingRepository;
import com.subscript.subscription.service.repository.SaasServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaasServiceServiceImpl implements SaasServiceService {

    private final SaasServiceRepository repository;
    private final MeterRepository meterRepository;
    private final PlanMeterMappingRepository planMeterMappingRepository;

    /**
     * Get all services.
     */
    public List<SaasService> getAllServices() {
        return repository.findAll();
    }

    /**
     * Get one service by ID.
     */
    public SaasService getServiceById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
    }

    /**
     * Create a new service.
     * Automatically provisions a default Meter for the service if one does not
     * already exist (duplicate-safe: we only create if the service has no meter).
     */
    @Override
    @Transactional
    public SaasService createService(SaasService service) {

        SaasService saved = repository.save(service);

        // Auto-provision: create one default meter for this service if none exists.
        List<Meter> existing = meterRepository.findByService_ServiceId(saved.getServiceId());
        if (existing.isEmpty()) {
            Meter meter = new Meter();
            meter.setService(saved);
            // Use the service name as the meter name; default unit is "api_calls".
            meter.setName(saved.getName() + " API Calls");
            meter.setUnit("api_calls");
            meterRepository.save(meter);
        }

        return saved;
    }

    /**
     * Update an existing service.
     */
    public SaasService updateService(Integer id, SaasService updatedService) {

        SaasService existingService = getServiceById(id);

        existingService.setName(updatedService.getName());
        existingService.setDescription(updatedService.getDescription());
        existingService.setCategory(updatedService.getCategory());
        existingService.setIsActive(updatedService.getIsActive());

        return repository.save(existingService);
    }

    /**
     * Delete a service.
     * Deletes the service's Meter only when no PlanMeterMapping rows reference it
     * (preserves referential integrity for plans still in use).
     */
    @Override
    @Transactional
    public void deleteService(Integer id) {

        SaasService service = getServiceById(id);

        // Delete orphaned meters (those with no plan mapping referencing them).
        List<Meter> meters = meterRepository.findByService_ServiceId(id);
        for (Meter meter : meters) {
            boolean hasMapping = !planMeterMappingRepository
                    .findByMeter_MeterId(meter.getMeterId()).isEmpty();
            if (!hasMapping) {
                meterRepository.delete(meter);
            }
        }

        repository.delete(service);
    }
}