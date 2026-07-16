package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.response.DashboardResponse;
import org.springframework.http.ResponseEntity;

public interface ReportsController {

    ResponseEntity<DashboardResponse> getSummaryReport();
}
