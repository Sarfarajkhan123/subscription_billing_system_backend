package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.service.service.interfaces.SaasServiceService;

import com.subscript.subscription.api.model.SaasService;
import com.subscript.subscription.service.repository.SaasServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SaasServiceServiceImpl implements SaasServiceService {

    // Spring injects the repository automatically
    private final SaasServiceRepository repository;

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
     */
    public SaasService createService(SaasService service) {

        return repository.save(service);
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
     */
    public void deleteService(Integer id) {

        SaasService service = getServiceById(id);

        repository.delete(service);
    }

}