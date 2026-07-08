package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.Payment;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PaymentController {

    ResponseEntity<Payment> processPayment(
            Integer invoiceId,
            Integer customerId,
            String paymentMethod);

    ResponseEntity<List<Payment>> getAllPayments();

    ResponseEntity<List<Payment>> getByCustomer(Integer customerId);

    ResponseEntity<List<Payment>> getByInvoice(Integer invoiceId);

    ResponseEntity<Payment> getById(Integer id);

    ResponseEntity<String> deletePayment(Integer id);

}