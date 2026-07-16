package com.subscript.subscription.api.wrapper.request;

import lombok.Data;

/** Razorpay checkout callback payload, verified server-side against the secret. */
@Data
public class PaymentVerifyRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
