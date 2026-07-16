package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.Discount;
import com.subscript.subscription.api.model.Invoice;
import com.subscript.subscription.api.model.Subscription;
import com.subscript.subscription.api.wrapper.mapper.InvoiceMapper;
import com.subscript.subscription.api.wrapper.request.InvoiceGenerateRequest;
import com.subscript.subscription.api.wrapper.response.InvoiceResponse;
import com.subscript.subscription.api.wrapper.response.UsageSummaryResponse;
import com.subscript.subscription.service.repository.InvoiceRepository;
import com.subscript.subscription.service.repository.SubscriptionRepository;
import com.subscript.subscription.service.service.interfaces.InvoiceService;
import com.subscript.subscription.service.service.interfaces.UsageDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;

    // Reuse the meter-based usage calculation (same one the Usage Summary /
    // Simulator use) as the single source of overage. @Lazy breaks the
    // Invoice <-> Usage service cycle.
    @Autowired
    @Lazy
    private UsageDataService usageDataService;

    @Override
    @Transactional
    public InvoiceResponse generateInvoice(InvoiceGenerateRequest request) {

        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found : " + request.getSubscriptionId()));

        // Billing period is derived from the subscription's renewal date so the
        // same cycle always maps to the same [start, end] — this is what makes
        // the duplicate guard below deterministic across callers (manual
        // generate, usage-overage trigger, and the billing-cycle job).
        LocalDate periodEnd;
        LocalDate periodStart;
        if (subscription.getRenewalDate() != null) {
            periodEnd = subscription.getRenewalDate();
            periodStart = periodEnd.minusMonths(1);
        } else {
            periodStart = LocalDate.now();
            periodEnd = periodStart.plusMonths(1);
        }

        // Prevent duplicate invoices for the same subscription + billing period:
        // if one already exists, return it instead of creating another.
        Optional<Invoice> existing = invoiceRepository
                .findFirstBySubscription_SubscriptionIdAndPeriodStartAndPeriodEnd(
                        subscription.getSubscriptionId(), periodStart, periodEnd);
        if (existing.isPresent()) {
            return InvoiceMapper.toResponse(existing.get());
        }

        BigDecimal baseAmount = subscription.getPlan().getBasePrice();
        if (baseAmount == null) {
            baseAmount = BigDecimal.ZERO;
        }

        // Reapply the subscription's coupon (if it is still valid) by reusing the
        // discount recorded on its most recent discounted invoice. Recurring
        // reuse does NOT re-increment currentUses — that redemption was counted
        // once at checkout.
        Discount appliedDiscount = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        Optional<Invoice> lastDiscounted = invoiceRepository
                .findTopBySubscription_SubscriptionIdAndDiscountIsNotNullOrderByIssuedAtDesc(
                        subscription.getSubscriptionId());
        if (lastDiscounted.isPresent() && lastDiscounted.get().getDiscount() != null) {
            Discount d = lastDiscounted.get().getDiscount();
            LocalDate today = LocalDate.now();
            boolean stillValid = Boolean.TRUE.equals(d.getIsActive())
                    && (d.getStartDate() == null || !today.isBefore(d.getStartDate()))
                    && (d.getEndDate() == null || !today.isAfter(d.getEndDate()));
            if (stillValid) {
                appliedDiscount = d;
                if (d.getDiscountType() == Discount.DiscountType.percentage) {
                    discountAmount = baseAmount
                            .multiply(d.getDiscountValue())
                            .divide(BigDecimal.valueOf(100));
                } else { // fixed_amount
                    discountAmount = d.getDiscountValue();
                }
                if (discountAmount.compareTo(baseAmount) > 0) {
                    discountAmount = baseAmount; // never below zero
                }
            }
        }

        // Usage overage — reuse the meter-based Usage Summary calculation
        // (plan_meter_mapping free_units / max_limit / price_per_unit over
        // cumulative usage) so the invoice, Usage Summary and Simulator always
        // agree. No meter => summary overage is null => no overage this cycle.
        UsageSummaryResponse usageSummary = usageDataService
                .getUsageSummary(subscription.getSubscriptionId());
        BigDecimal overage = usageSummary.getOverage();
        if (overage == null) {
            overage = BigDecimal.ZERO;
        }

        BigDecimal netBase = baseAmount.subtract(discountAmount);
        BigDecimal tax = netBase.multiply(BigDecimal.valueOf(0.18));
        BigDecimal total = netBase.add(tax).add(overage);

        Invoice invoice = new Invoice();
        invoice.setCustomer(subscription.getCustomer());
        invoice.setSubscription(subscription);
        invoice.setInvoiceNumber(
                "INV-" + UUID.randomUUID().toString().substring(0, 8));
        invoice.setBaseAmount(baseAmount);
        invoice.setDiscountAmount(discountAmount);
        invoice.setDiscount(appliedDiscount);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);
        invoice.setPeriodStart(periodStart);
        invoice.setPeriodEnd(periodEnd);
        invoice.setDueDate(LocalDate.now().plusDays(15));
        invoice.setStatus(Invoice.Status.pending);

        Invoice saved = invoiceRepository.save(invoice);

        return InvoiceMapper.toResponse(saved);
    }

    /**
     * Billing-cycle automation. Runs daily; for every active subscription whose
     * renewal date has arrived it generates the cycle invoice (reusing the
     * duplicate-guarded {@link #generateInvoice}) and advances the renewal date
     * so the next cycle bills a month later.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void runBillingCycle() {
        List<Subscription> due = subscriptionRepository
                .findByStatusAndRenewalDateLessThanEqual(
                        Subscription.Status.active, LocalDate.now());
        for (Subscription sub : due) {
            try {
                InvoiceGenerateRequest req = new InvoiceGenerateRequest();
                req.setSubscriptionId(sub.getSubscriptionId());
                generateInvoice(req);
                LocalDate anchor = sub.getRenewalDate() == null
                        ? LocalDate.now()
                        : sub.getRenewalDate();
                sub.setRenewalDate(anchor.plusMonths(1));
                subscriptionRepository.save(sub);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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