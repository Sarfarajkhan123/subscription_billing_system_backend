package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.model.Invoice;
import com.subscript.subscription.api.model.Payment;
import com.subscript.subscription.api.wrapper.mapper.PaymentMapper;
import com.subscript.subscription.api.wrapper.request.PaymentRequest;
import com.subscript.subscription.api.wrapper.response.PaymentResponse;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.InvoiceRepository;
import com.subscript.subscription.service.repository.PaymentRepository;
import com.subscript.subscription.service.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + request.getInvoiceId()));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + request.getCustomerId()));

        // Ensure invoice belongs to customer
        if (!invoice.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("Invoice does not belong to the given customer.");
        }

        // Prevent duplicate payment
        if (invoice.getStatus() == Invoice.Status.paid) {
            throw new RuntimeException("Invoice is already paid.");
        }

        Payment payment = new Payment();

        payment.setInvoice(invoice);
        payment.setCustomer(customer);

        // Pay full invoice amount
        payment.setAmountPaid(invoice.getTotalAmount());

        payment.setPaymentMethod(
                request.getPaymentMethod() == null
                        ? "mock_card"
                        : request.getPaymentMethod());

        payment.setTransactionId(
                "TXN-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase());

        // Payment successful
        payment.setStatus(Payment.Status.success);
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Mark invoice as paid
        invoice.setStatus(Invoice.Status.paid);
        invoice.setPaidAt(LocalDateTime.now());

        invoiceRepository.save(invoice);

        return PaymentMapper.toResponse(savedPayment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomer(Integer customerId) {

        return paymentRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(PaymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByInvoice(Integer invoiceId) {

        return paymentRepository.findByInvoice_InvoiceId(invoiceId)
                .stream()
                .map(PaymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getPaymentById(Integer id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));

        return PaymentMapper.toResponse(payment);
    }

    @Override
    public void deletePayment(Integer id) {

        paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));

        paymentRepository.deleteById(id);
    }

    @Override
    public PaymentResponse verifyPayment(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        payment.setStatus(Payment.Status.success);
        paymentRepository.save(payment);
        
        return PaymentMapper.toResponse(payment);
    }
}