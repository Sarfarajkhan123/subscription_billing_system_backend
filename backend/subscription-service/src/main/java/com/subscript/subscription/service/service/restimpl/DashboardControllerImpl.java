package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.DashboardController;
import com.subscript.subscription.api.wrapper.response.DashboardResponse;
import com.subscript.subscription.service.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardControllerImpl implements DashboardController {

    private final DashboardService dashboardService;

    // GET /api/dashboard — overview metrics (Finance / IT Admin)
    @Override
    @GetMapping
    public ResponseEntity<DashboardResponse> getOverview() {
        return ResponseEntity.ok(dashboardService.getOverview());
    }
}
