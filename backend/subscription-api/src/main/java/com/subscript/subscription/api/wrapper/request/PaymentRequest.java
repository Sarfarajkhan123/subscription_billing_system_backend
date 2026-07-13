package com.subscript.subscription.api.wrapper.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    private Integer invoiceId;

    private String paymentMethod;

    private BigDecimal amount;

    private Integer customerId;

}