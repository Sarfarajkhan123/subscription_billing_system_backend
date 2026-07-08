package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.UsageDataController;
import com.subscript.subscription.api.model.UsageData;
import com.subscript.subscription.service.service.interfaces.UsageDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsageDataControllerImpl implements UsageDataController {

    private final UsageDataService usageDataService;

    /**
     * POST /api/usage/record
     * ?subscriptionId=1 &apiCalls=100 &activeUsers=5 &storageGb=10 [&meterId=1]
     *
     * Returns 400 if meterId is not mapped to the plan.
     * Returns 409 if subscription is expired or cancelled.
     */
    @Override
    @PostMapping("/record")
    public ResponseEntity<?> recordUsage(
            @RequestParam Integer subscriptionId,
            @RequestParam(required = false) Integer meterId,
            @RequestParam(defaultValue = "0") Integer apiCalls,
            @RequestParam(defaultValue = "0") Integer activeUsers,
            @RequestParam(defaultValue = "0") BigDecimal storageGb) {

        try {
            UsageData saved = usageDataService.recordUsage(
                    subscriptionId, meterId, apiCalls, activeUsers, storageGb);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/usage/subscription/{subscriptionId}
    @Override
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<UsageData>> getBySubscription(
            @PathVariable Integer subscriptionId) {
        return ResponseEntity.ok(usageDataService.getUsageBySubscription(subscriptionId));
    }

    // GET /api/usage/subscription/{subscriptionId}/latest
    @Override
    @GetMapping("/subscription/{subscriptionId}/latest")
    public ResponseEntity<UsageData> getLatest(
            @PathVariable Integer subscriptionId) {
        return ResponseEntity.ok(usageDataService.getLatestUsage(subscriptionId));
    }

    // GET /api/usage/{id}
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UsageData> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(usageDataService.getUsageById(id));
    }

    // DELETE /api/usage/{id}
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsage(
            @PathVariable Integer id) {
        usageDataService.deleteUsage(id);
        return ResponseEntity.ok("Usage record deleted");
    }
}