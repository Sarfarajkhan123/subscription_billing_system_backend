package com.subscript.subscription.api.wrapper.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageDataResponse {
    private Integer usageId;
    private Integer subscriptionId;
    private Integer saasServiceId;
    private Integer meterId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal value;
    private String unit;
    private Integer apiCalls;
    private Integer activeUsers;
    private BigDecimal storageGb;
    private BigDecimal overageCharge;
    private LocalDateTime recordedAt;
}
