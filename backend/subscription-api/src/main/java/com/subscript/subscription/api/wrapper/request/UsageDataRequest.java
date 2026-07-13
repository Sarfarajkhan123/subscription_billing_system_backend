package com.subscript.subscription.api.wrapper.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageDataRequest {
    private Integer subscriptionId;
    private Integer meterId;
    private Integer apiCalls;
    private Integer activeUsers;
    private BigDecimal storageGb;
}
