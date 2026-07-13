package com.subscript.subscription.api.wrapper.request;

import lombok.Data;

@Data
public class SubscribeRequest {
    private Integer planId;
    private String couponCode;
}
