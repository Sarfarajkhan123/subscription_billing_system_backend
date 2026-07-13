package com.subscript.subscription.api.wrapper.mapper;

import com.subscript.subscription.api.model.Payment;
import com.subscript.subscription.api.wrapper.response.PaymentResponse;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {

        PaymentResponse response = new PaymentResponse();

        response.setPaymentId(payment.getPaymentId());

        if (payment.getInvoice() != null) {
            response.setInvoiceId(payment.getInvoice().getInvoiceId());
            response.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
        }

        response.setTransactionId(payment.getTransactionId());
        response.setAmount(payment.getAmountPaid());
        response.setPaymentMethod(payment.getPaymentMethod());

        if (payment.getStatus() != null) {
            response.setStatus(payment.getStatus().name());
        }

        response.setPaidAt(payment.getPaymentDate());

        return response;
    }
}
