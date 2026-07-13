package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.Invoice;
import com.subscript.subscription.api.model.Subscription;
import com.subscript.subscription.api.model.UsageData;
import com.subscript.subscription.api.wrapper.mapper.InvoiceMapper;
import com.subscript.subscription.api.wrapper.request.InvoiceGenerateRequest;
import com.subscript.subscription.api.wrapper.response.InvoiceResponse;
import com.subscript.subscription.service.repository.InvoiceRepository;
import com.subscript.subscription.service.repository.SubscriptionRepository;
import com.subscript.subscription.service.repository.UsageDataRepository;
import com.subscript.subscription.service.service.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UsageDataRepository usageDataRepository;

    @Override
    public InvoiceResponse generateInvoice(InvoiceGenerateRequest request) {

        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found : " + request.getSubscriptionId()));

        UsageData usage = usageDataRepository
                .findTopBySubscription_SubscriptionIdOrderByRecordedAtDesc(
                        subscription.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("No usage found for subscription"));

        BigDecimal baseAmount = subscription.getPlan().getBasePrice();

        if (baseAmount == null) {
            baseAmount = BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal tax = baseAmount.multiply(BigDecimal.valueOf(0.18));

        BigDecimal total = baseAmount
                .subtract(discount)
                .add(tax)
                .add(
                        usage.getOverageCharge() == null
                                ? BigDecimal.ZERO
                                : usage.getOverageCharge());

        Invoice invoice = new Invoice();

        invoice.setCustomer(subscription.getCustomer());
        invoice.setSubscription(subscription);

        invoice.setInvoiceNumber(
                "INV-" + UUID.randomUUID().toString().substring(0, 8));

        invoice.setBaseAmount(baseAmount);

        invoice.setDiscountAmount(discount);

        invoice.setTaxAmount(tax);

        invoice.setTotalAmount(total);

        invoice.setPeriodStart(usage.getPeriodStart());

        invoice.setPeriodEnd(usage.getPeriodEnd());

        invoice.setDueDate(LocalDate.now().plusDays(15));

        invoice.setStatus(Invoice.Status.pending);

        Invoice saved = invoiceRepository.save(invoice);

        return InvoiceMapper.toResponse(saved);
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {

        return invoiceRepository.findAll()
                .stream()
                .map(InvoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> getInvoicesByCustomer(Integer customerId) {

        return invoiceRepository
                .findByCustomer_CustomerIdOrderByIssuedAtDesc(customerId)
                .stream()
                .map(InvoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> getInvoicesByStatus(String status) {

        Invoice.Status invoiceStatus = Invoice.Status.valueOf(status.toLowerCase());

        return invoiceRepository.findByStatus(invoiceStatus)
                .stream()
                .map(InvoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceResponse getInvoiceById(Integer id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found : " + id));

        return InvoiceMapper.toResponse(invoice);
    }

    @Override
    public InvoiceResponse markAsPaid(Integer id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus(Invoice.Status.paid);
        invoice.setPaidAt(java.time.LocalDateTime.now());

        return InvoiceMapper.toResponse(
                invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse markAsOverdue(Integer id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus(Invoice.Status.overdue);

        return InvoiceMapper.toResponse(
                invoiceRepository.save(invoice));
    }

    @Override
    public void deleteInvoice(Integer id) {

        invoiceRepository.deleteById(id);
    }
}