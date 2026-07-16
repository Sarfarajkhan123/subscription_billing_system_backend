package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.ReportsController;
import com.subscript.subscription.api.wrapper.response.DashboardResponse;
import com.subscript.subscription.service.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reports API. Access is restricted to FINANCE / IT_ADMIN in SecurityBeansConfig
 * (CUSTOMER / SUPPORT / PRODUCT receive 403). Reuses the existing
 * DashboardService as the single source of truth — no duplicated business logic.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReportsControllerImpl implements ReportsController {

    private final DashboardService dashboardService;

    // GET /api/reports/summary — financial summary report (Finance / IT Admin).
    @Override
    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getSummaryReport() {
        return ResponseEntity.ok(dashboardService.getOverview());
    }
}
