package com.subscript.subscription.api.wrapper.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateDiscountRequest {
    private String couponCode;
    private String discountType; // "percentage" or "fixed_amount"
    private BigDecimal discountValue;
    private Integer maxUses;
    private String startDate; // "2026-01-01"
    private String endDate;
}

