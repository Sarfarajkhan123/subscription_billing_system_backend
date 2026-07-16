package com.subscript.subscription.api.wrapper.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data the frontend needs to open Razorpay Checkout. Contains only the PUBLIC
 * key id — the Razorpay secret never leaves the backend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private String orderId;
    private long amount;      // in the smallest currency unit (paise)
    private String currency;
    private String keyId;     // public Razorpay key id
    private String invoiceNumber;
    private Integer paymentId;
}
