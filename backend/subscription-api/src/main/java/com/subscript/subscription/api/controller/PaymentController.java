package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.request.PaymentRequest;
import com.subscript.subscription.api.wrapper.response.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface PaymentController {

    ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request);

    ResponseEntity<List<PaymentResponse>> getAllPayments();

    ResponseEntity<List<PaymentResponse>> getByCustomer(
            Integer customerId);

    ResponseEntity<List<PaymentResponse>> getByInvoice(
            Integer invoiceId);

    ResponseEntity<PaymentResponse> getById(
            Integer id);

    ResponseEntity<String> deletePayment(
            Integer id);

}