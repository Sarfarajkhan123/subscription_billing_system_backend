package com.subscript.subscription.api.wrapper.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Integer paymentId;

    private Integer invoiceId;

    private String invoiceNumber;

    private String transactionId;

    private BigDecimal amount;

    private String paymentMethod;

    private String status;

    private LocalDateTime paidAt;

}
