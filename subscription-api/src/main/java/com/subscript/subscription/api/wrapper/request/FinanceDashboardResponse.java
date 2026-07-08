package com.subscript.subscription.api.wrapper.request;
import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FinanceDashboardResponse {
    private BigDecimal mrr;
    private BigDecimal arr;
    private long overdueCount;
    private long pendingCount;
    private long activeSubscriptions;
}