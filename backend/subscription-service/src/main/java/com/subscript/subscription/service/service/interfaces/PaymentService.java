package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.wrapper.request.PaymentRequest;
import com.subscript.subscription.api.wrapper.request.PaymentVerifyRequest;
import com.subscript.subscription.api.wrapper.response.PaymentOrderResponse;
import com.subscript.subscription.api.wrapper.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    // Razorpay: start checkout for an invoice (owned by customerId) and finalize
    // it after Checkout returns, verifying the signature server-side.
    PaymentOrderResponse createRazorpayOrder(Integer invoiceId, Integer customerId);

    PaymentResponse verifyRazorpayPayment(PaymentVerifyRequest request);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByCustomer(Integer customerId);

    List<PaymentResponse> getPaymentsByInvoice(Integer invoiceId);

    PaymentResponse getPaymentById(Integer id);

    PaymentResponse verifyPayment(Integer paymentId);

    void deletePayment(Integer id);
}
