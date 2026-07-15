package com.subscript.subscription.api.wrapper.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Overview metrics for the admin/finance dashboard. Computed on demand from the
 * existing repositories — no stored aggregate, no new table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private BigDecimal mrr;                 // monthly recurring revenue (active subs, normalised to monthly)

    private BigDecimal arr;                 // mrr * 12

    private long activeSubscriptions;       // subscriptions with status = active

    private long totalCustomers;            // all customer records

    private BigDecimal revenue;             // sum of paid invoices' total amount

    private long paidInvoices;              // invoices with status = paid

    private long pendingInvoices;           // invoices with status = pending

    private BigDecimal payments;            // sum of successful payments
}
