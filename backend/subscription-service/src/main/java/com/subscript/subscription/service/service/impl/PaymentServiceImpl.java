package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.model.Invoice;
import com.subscript.subscription.api.model.Payment;
import com.subscript.subscription.api.wrapper.mapper.PaymentMapper;
import com.subscript.subscription.api.wrapper.request.PaymentRequest;
import com.subscript.subscription.api.wrapper.request.PaymentVerifyRequest;
import com.subscript.subscription.api.wrapper.response.PaymentOrderResponse;
import com.subscript.subscription.api.wrapper.response.PaymentResponse;
import com.subscript.subscription.service.gateway.RazorpayGateway;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.InvoiceRepository;
import com.subscript.subscription.service.repository.PaymentRepository;
import com.subscript.subscription.service.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final RazorpayGateway razorpayGateway;

    private static final String CURRENCY = "INR";

    /**
     * Razorpay step 1 — create an order for the customer's unpaid invoice and a
     * matching pending Payment row. Reuses the same ownership + already-paid
     * guards as {@link #processPayment}.
     */
    @Override
    @Transactional
    public PaymentOrderResponse createRazorpayOrder(Integer invoiceId, Integer customerId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Invoice not found: " + invoiceId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found: " + customerId));

        if (!invoice.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only pay your own invoices.");
        }

        // Prevent paying an already-paid invoice.
        if (invoice.getStatus() == Invoice.Status.paid) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Invoice is already paid.");
        }

        long amountPaise = invoice.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        String orderId = razorpayGateway.createOrder(
                amountPaise, CURRENCY, invoice.getInvoiceNumber());

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setCustomer(customer);
        payment.setAmountPaid(invoice.getTotalAmount());
        payment.setPaymentMethod("razorpay");
        payment.setRazorpayOrderId(orderId);
        payment.setStatus(Payment.Status.pending);
        Payment saved = paymentRepository.save(payment);

        return new PaymentOrderResponse(
                orderId,
                amountPaise,
                CURRENCY,
                razorpayGateway.getKeyId(),
                invoice.getInvoiceNumber(),
                saved.getPaymentId());
    }

    /**
     * Razorpay step 2 — verify the Checkout signature server-side, then finalize:
     * mark the payment successful and the invoice paid. Rejects tampered
     * signatures and already-paid invoices (duplicate protection).
     */
    @Override
    @Transactional
    public PaymentResponse verifyRazorpayPayment(PaymentVerifyRequest request) {

        Payment payment = paymentRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payment order not found."));

        Invoice invoice = payment.getInvoice();
        if (invoice.getStatus() == Invoice.Status.paid) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Invoice is already paid.");
        }

        boolean valid = razorpayGateway.isValidSignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature());
        if (!valid) {
            payment.setStatus(Payment.Status.failed);
            paymentRepository.save(payment);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Payment signature verification failed.");
        }

        payment.setTransactionId(request.getRazorpayPaymentId());
        payment.setStatus(Payment.Status.success);
        payment.setPaymentDate(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        invoice.setStatus(Invoice.Status.paid);
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        return PaymentMapper.toResponse(saved);
    }

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