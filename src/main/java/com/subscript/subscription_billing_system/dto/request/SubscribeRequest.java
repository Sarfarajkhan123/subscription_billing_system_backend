package com.subscript.subscription_billing_system.dto.request;

import lombok.Data;

@Data
public class SubscribeRequest {
    private Integer planId;
    private String couponCode;
}
