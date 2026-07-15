package com.subscript.subscription.api.wrapper.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Meter-based usage summary for one subscription, computed on the backend from
 * subscription → plan → plan_meter_mapping → meter. All figures are calculated
 * server-side (the frontend only displays them).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageSummaryResponse {

    private Integer subscriptionId;

    /** False when the plan has no meter mapping — the UI shows a message. */
    private boolean hasMeter;

    private String meterName;

    private String unit;

    private Integer freeUnits;      // included units before overage applies

    private Integer usageLimit;     // plan_meter_mapping.max_limit

    private BigDecimal pricePerUnit;

    private long currentUsage;      // total recorded units (apiCalls) for the subscription

    private long remaining;         // max(0, usageLimit - currentUsage)

    private double usagePercent;    // currentUsage / usageLimit * 100 (may exceed 100)

    private BigDecimal overage;     // max(0, currentUsage - freeUnits) * pricePerUnit
}
