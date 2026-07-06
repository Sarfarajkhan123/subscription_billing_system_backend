package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.UsageData;
import com.subscript.subscription_billing_system.service.UsageDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsageDataController {

    private final UsageDataService usageDataService;

    // POST
    // /api/usage/record?subscriptionId=1&apiCalls=15000&activeUsers=8&storageGb=2.5
    @PostMapping("/record")
    public ResponseEntity<UsageData> recordUsage(
            @RequestParam Integer subscriptionId,
            @RequestParam Integer apiCalls,
            @RequestParam Integer activeUsers,
            @RequestParam(defaultValue = "0") BigDecimal storageGb) {
        return ResponseEntity.ok(
                usageDataService.recordUsage(subscriptionId, apiCalls, activeUsers, storageGb));
    }

    // GET /api/usage/subscription/1
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<UsageData>> getBySubscription(
            @PathVariable Integer subscriptionId) {
        return ResponseEntity.ok(usageDataService.getUsageBySubscription(subscriptionId));
    }

    // GET /api/usage/subscription/1/latest
    @GetMapping("/subscription/{subscriptionId}/latest")
    public ResponseEntity<UsageData> getLatest(@PathVariable Integer subscriptionId) {
        return ResponseEntity.ok(usageDataService.getLatestUsage(subscriptionId));
    }

    // GET /api/usage/1
    @GetMapping("/{id}")
    public ResponseEntity<UsageData> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(usageDataService.getUsageById(id));
    }

    // DELETE /api/usage/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsage(@PathVariable Integer id) {
        usageDataService.deleteUsage(id);
        return ResponseEntity.ok("Usage record deleted");
    }
}