package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.SaasServiceController;
import com.subscript.subscription.api.model.SaasService;
import com.subscript.subscription.service.service.interfaces.SaasServiceService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SaasServiceControllerImpl implements SaasServiceController {

    private final SaasServiceService saasServiceService;

    // GET /api/services
    @Override
    @GetMapping
    public ResponseEntity<List<SaasService>> getAllServices() {

        return ResponseEntity.ok(
                saasServiceService.getAllServices());
    }

    // GET /api/services/{id}
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<SaasService> getServiceById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                saasServiceService.getServiceById(id));
    }

    // POST /api/services
    @Override
    @PostMapping
    public ResponseEntity<SaasService> createService(
            @RequestBody SaasService service) {

        return ResponseEntity.ok(
                saasServiceService.createService(service));
    }

    // PUT /api/services/{id}
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<SaasService> updateService(
            @PathVariable Integer id,
            @RequestBody SaasService updatedService) {

        return ResponseEntity.ok(
                saasServiceService.updateService(id, updatedService));
    }

    // DELETE /api/services/{id}
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(
            @PathVariable Integer id) {

        saasServiceService.deleteService(id);

        return ResponseEntity.ok("Service deleted successfully.");
    }
}