package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.SaasService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SaasServiceController {

    ResponseEntity<List<SaasService>> getAllServices();

    ResponseEntity<SaasService> getServiceById(Integer id);

    ResponseEntity<SaasService> createService(SaasService service);

    ResponseEntity<SaasService> updateService(
            Integer id,
            SaasService updatedService);

    ResponseEntity<String> deleteService(Integer id);

}