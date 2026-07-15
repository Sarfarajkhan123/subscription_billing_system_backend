package com.subscript.subscription.api.wrapper.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Integer subscriptionId;

    private Integer customerId;

    private Integer serviceId;

    private Integer planId;

    private String serviceName;

    private String planName;

    private BigDecimal basePrice;

    private String billingCycle;

    private String status;

    private LocalDate startDate;

    private LocalDate trialEndDate;

    private LocalDate renewalDate;

    private String apiKey;

    // Billing summary for the subscribe response (final amount after coupon).
    private BigDecimal discountAmount;

    private BigDecimal finalAmount;
}