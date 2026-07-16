package com.subscript.subscription.api.wrapper.request;

import lombok.Data;

/** Body to start a Razorpay checkout for one unpaid invoice. */
@Data
public class PaymentOrderRequest {
    private Integer invoiceId;
}
