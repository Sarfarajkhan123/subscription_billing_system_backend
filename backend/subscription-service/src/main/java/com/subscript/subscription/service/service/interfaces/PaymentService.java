package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.wrapper.request.PaymentRequest;
import com.subscript.subscription.api.wrapper.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByCustomer(Integer customerId);

    List<PaymentResponse> getPaymentsByInvoice(Integer invoiceId);

    PaymentResponse getPaymentById(Integer id);

    PaymentResponse verifyPayment(Integer paymentId);

    void deletePayment(Integer id);
}
