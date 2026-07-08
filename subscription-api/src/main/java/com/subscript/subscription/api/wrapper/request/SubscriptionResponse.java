package com.subscript.subscription.api.wrapper.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SubscriptionResponse {
    private Integer subscriptionId;
    private String serviceName;
    private String planName;
    private BigDecimal basePrice;
    private String billingCycle;
    private String status;
    private String renewalDate;
    private String apiKey;
}