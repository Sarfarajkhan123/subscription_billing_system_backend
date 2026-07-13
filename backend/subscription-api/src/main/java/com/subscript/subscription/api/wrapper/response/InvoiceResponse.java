package com.subscript.subscription.api.wrapper.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InvoiceResponse {

    private Integer invoiceId;

    private String invoiceNumber;

    private Integer customerId;

    private Integer subscriptionId;

    private BigDecimal baseAmount;

    private BigDecimal discountAmount;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private String status;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private LocalDate dueDate;

    private LocalDateTime issuedAt;

}