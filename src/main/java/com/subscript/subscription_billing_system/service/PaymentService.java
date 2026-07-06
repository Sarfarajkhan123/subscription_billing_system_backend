package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.*;
import com.subscript.subscription_billing_system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    // PROCESS PAYMENT — pays an invoice and marks it as paid
    public Payment processPayment(Integer invoiceId, Integer customerId, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        if (invoice.getStatus() == Invoice.Status.paid) {
            throw new RuntimeException("Invoice is already paid");
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setCustomer(customer);
        payment.setAmountPaid(invoice.getTotalAmount());
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "mock_card");
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus(Payment.Status.success);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        // Mark the invoice as paid
        invoice.setStatus(Invoice.Status.paid);
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        return payment;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByCustomer(Integer customerId) {
        return paymentRepository.findByCustomer_CustomerId(customerId);
    }

    public List<Payment> getPaymentsByInvoice(Integer invoiceId) {
        return paymentRepository.findByInvoice_InvoiceId(invoiceId);
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }

    public void deletePayment(Integer id) {
        getPaymentById(id);
        paymentRepository.deleteById(id);
    }
}