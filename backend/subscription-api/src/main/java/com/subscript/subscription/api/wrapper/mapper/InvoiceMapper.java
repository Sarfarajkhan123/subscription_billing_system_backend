package com.subscript.subscription.api.wrapper.mapper;

import com.subscript.subscription.api.model.Invoice;
import com.subscript.subscription.api.wrapper.response.InvoiceResponse;

public class InvoiceMapper {

    private InvoiceMapper() {
    }

    public static InvoiceResponse toResponse(Invoice invoice) {

        InvoiceResponse response = new InvoiceResponse();

        response.setInvoiceId(invoice.getInvoiceId());

        response.setInvoiceNumber(invoice.getInvoiceNumber());

        if (invoice.getCustomer() != null) {
            response.setCustomerId(
                    invoice.getCustomer().getCustomerId());
        }

        if (invoice.getSubscription() != null) {
            response.setSubscriptionId(
                    invoice.getSubscription().getSubscriptionId());
        }

        response.setBaseAmount(invoice.getBaseAmount());
        response.setDiscountAmount(invoice.getDiscountAmount());
        response.setTaxAmount(invoice.getTaxAmount());
        response.setTotalAmount(invoice.getTotalAmount());

        response.setStatus(invoice.getStatus().name());

        response.setPeriodStart(invoice.getPeriodStart());
        response.setPeriodEnd(invoice.getPeriodEnd());

        response.setDueDate(invoice.getDueDate());
        response.setIssuedAt(invoice.getIssuedAt());

        return response;
    }
}