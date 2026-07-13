package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.UsageDataController;
import com.subscript.subscription.api.wrapper.request.UsageDataRequest;
import com.subscript.subscription.api.wrapper.response.UsageDataResponse;
import com.subscript.subscription.service.service.interfaces.UsageDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     */
    @Override
    @PostMapping("/record")
    public ResponseEntity<UsageDataResponse> recordUsage(
            @RequestBody UsageDataRequest request) {

        System.out.println("========== REQUEST RECEIVED ==========");
        System.out.println(request);

        try {

            UsageDataResponse response = usageDataService.recordUsage(request);

            System.out.println("========== USAGE SAVED ==========");
            System.out.println(response);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().build();

        } catch (IllegalStateException e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/usage/subscription/{subscriptionId}
     */
    @Override
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<UsageDataResponse>> getBySubscription(
            @PathVariable Integer subscriptionId) {

        try {
            return ResponseEntity.ok(
                    usageDataService.getUsageBySubscription(subscriptionId));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/usage/subscription/{subscriptionId}/latest
     */
    @Override
    @GetMapping("/subscription/{subscriptionId}/latest")
    public ResponseEntity<UsageDataResponse> getLatest(
            @PathVariable Integer subscriptionId) {

        try {
            return ResponseEntity.ok(
                    usageDataService.getLatestUsage(subscriptionId));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/usage/{id}
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UsageDataResponse> getById(
            @PathVariable Integer id) {

        try {
            return ResponseEntity.ok(
                    usageDataService.getUsageById(id));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /api/usage/{id}
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsage(
            @PathVariable Integer id) {

        try {

            usageDataService.deleteUsage(id);

            return ResponseEntity.ok("Usage record deleted");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete usage record.");
        }
    }
}