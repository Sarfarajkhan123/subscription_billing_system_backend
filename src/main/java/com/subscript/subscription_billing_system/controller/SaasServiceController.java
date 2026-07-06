package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.SaasService;
import com.subscript.subscription_billing_system.entity.SubscriptionPlan;
import com.subscript.subscription_billing_system.repository.SaasServiceRepository;
import com.subscript.subscription_billing_system.repository.SubscriptionPlanRepository;
import com.subscript.subscription_billing_system.service.SaasServiceService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SaasServiceController {

        private final SaasServiceService saasServiceService;

        @GetMapping
        public ResponseEntity<List<SaasService>> getAllServices() {
                return ResponseEntity.ok(
                                saasServiceService.getAllServices());
        }

        @GetMapping("/{id}")
        public ResponseEntity<SaasService> getServiceById(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                saasServiceService.getServiceById(id));
        }

        @PostMapping
        public ResponseEntity<SaasService> createService(
                        @RequestBody SaasService service) {

                return ResponseEntity.ok(
                                saasServiceService.createService(service));
        }

        @PutMapping("/{id}")
        public ResponseEntity<SaasService> updateService(
                        @PathVariable Integer id,
                        @RequestBody SaasService updatedService) {

                return ResponseEntity.ok(
                                saasServiceService.updateService(id, updatedService));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteService(
                        @PathVariable Integer id) {

                saasServiceService.deleteService(id);

                return ResponseEntity.ok("Service deleted successfully.");
        }
}