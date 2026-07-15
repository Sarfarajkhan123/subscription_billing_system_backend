package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.UsageDataController;
import com.subscript.subscription.api.wrapper.request.UsageDataRequest;
import com.subscript.subscription.api.wrapper.response.UsageDataResponse;
import com.subscript.subscription.api.wrapper.response.UsageSummaryResponse;
import com.subscript.subscription.service.service.interfaces.CustomerService;
import com.subscript.subscription.service.service.interfaces.SubscriptionService;
import com.subscript.subscription.service.service.interfaces.UsageDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsageDataControllerImpl implements UsageDataController {

    private final UsageDataService usageDataService;
    private final SubscriptionService subscriptionService;
    private final CustomerService customerService;

    /**
     * POST /api/usage/record
     */
    @Override
    @PostMapping("/record")
    public ResponseEntity<UsageDataResponse> recordUsage(
            @RequestBody UsageDataRequest request) {

        // RBAC: only the owning customer or an IT Admin may generate usage.
        enforceCanModifyUsage(request.getSubscriptionId());

        System.out.println("========== REQUEST RECEIVED ==========");
        System.out.println(request);

        try {

            UsageDataResponse response = usageDataService.recordUsage(request);

            System.out.println("========== USAGE SAVED ==========");
            System.out.println(response);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // e.g. meter not mapped to the plan — surface the reason to the client
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (IllegalStateException e) {
            // business rule: cannot record usage for expired/cancelled subscriptions
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * GET /api/usage/subscription/{subscriptionId}
     */
    @Override
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<UsageDataResponse>> getBySubscription(
            @PathVariable Integer subscriptionId) {

        // RBAC: a customer may only view their own subscription's usage; staff
        // (product/finance/support/it_admin) may view any.
        enforceCanViewUsage(subscriptionId);

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

    /**
     * GET /api/usage/subscription/{subscriptionId}/summary
     * Meter-based usage metrics (limit / remaining / % / overage), all computed
     * server-side. Same view rule as usage history.
     */
    @Override
    @GetMapping("/subscription/{subscriptionId}/summary")
    public ResponseEntity<UsageSummaryResponse> getUsageSummary(
            @PathVariable Integer subscriptionId) {

        enforceCanViewUsage(subscriptionId);
        return ResponseEntity.ok(usageDataService.getUsageSummary(subscriptionId));
    }

    /**
     * DELETE /api/usage/subscription/{subscriptionId}
     * Resets (clears) all usage for one subscription — used by the simulator's
     * "Reset Demo". Same write rule as recording: owning customer or IT Admin.
     */
    @Override
    @DeleteMapping("/subscription/{subscriptionId}")
    public ResponseEntity<String> deleteBySubscription(
            @PathVariable Integer subscriptionId) {

        enforceCanModifyUsage(subscriptionId);
        usageDataService.deleteUsageBySubscription(subscriptionId);
        return ResponseEntity.ok("Usage reset for subscription " + subscriptionId);
    }

    // ── RBAC helpers ─────────────────────────────────────────────────────────

    /** The customerId that owns the given subscription. */
    private Integer subscriptionOwner(Integer subscriptionId) {
        return subscriptionService.getSubscriptionById(subscriptionId).getCustomerId();
    }

    /** True when the authenticated caller holds the given role. */
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    /**
     * WRITE (record / reset): ONLY the owning customer. The simulator represents
     * the customer's own application making API calls, so staff roles
     * (product / finance / support / it_admin) cannot generate usage — they
     * remain view-only via {@link #enforceCanViewUsage}.
     */
    private void enforceCanModifyUsage(Integer subscriptionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean owningCustomer = hasRole(auth, "ROLE_CUSTOMER")
                && customerService.getMyProfile(auth.getName()).getCustomerId()
                        .equals(subscriptionOwner(subscriptionId));
        if (!owningCustomer) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the owning customer can generate usage for this subscription.");
        }
    }

    /** VIEW: a customer only their own; staff roles may view any. */
    private void enforceCanViewUsage(Integer subscriptionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (hasRole(auth, "ROLE_CUSTOMER")
                && !customerService.getMyProfile(auth.getName()).getCustomerId()
                        .equals(subscriptionOwner(subscriptionId))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only view your own usage.");
        }
    }
}