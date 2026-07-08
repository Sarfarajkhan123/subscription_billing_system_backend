package com.subscript.subscription.api.wrapper.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceResponse {
    private Integer invoiceId;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private String status;
    private String dueDate;
    private String issuedAt;
    private String serviceName;
    private String planName;
}